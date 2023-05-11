package de.zahrie.trues.api.coverage.match;

import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import de.zahrie.trues.util.io.log.Console;
import de.zahrie.trues.util.io.log.DevInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ExtensionMethod(StringUtils.class)
public final class MatchResult implements Comparable<MatchResult> {
  public static final MatchResult ZERO = new MatchResult(0, 0);

  public static MatchResult fromResultString(String resultString, Match match) {
    return fromResultString(resultString, match.getFormat(), match.getLogs(MatchLogAction.REPORT));
  }

  public static MatchResult fromResultString(String resultString, MatchFormat format, int matchId) {
    final List<MatchLog> reportedLogs = new Query<>(MatchLog.class).where("coverage", matchId).and("action", MatchLogAction.REPORT).entityList();
    return fromResultString(resultString, format, reportedLogs);
  }

  public static MatchResult fromResultString(String resultString, MatchFormat format, List<MatchLog> logs) {
    if (resultString.matches("\\d+:\\d+")) {
      return new MatchResult(resultString.before(":").intValue(), resultString.after(":").intValue(), format.ordinal(), true);
    }

    if (!resultString.equals("-:-")) {
      new DevInfo(resultString).with(Console.class).error(new IllegalArgumentException("Das Ergebnis ist nicht gültig!"));
      return null;
    }

    final long homeWins = logs.stream().filter(log -> log.getParticipator().isHome()).count();
    final long guestWins = logs.stream().filter(log -> !log.getParticipator().isHome()).count();
    return new MatchResult((int) homeWins, (int) guestWins, format.ordinal(), false);
  }

  private final int homeScore;
  private final int guestScore;
  private final Integer maxGames;
  private final Boolean played;

  public MatchResult(int homeScore, int guestScore) {
    this(homeScore, guestScore, null, null);
  }

  public MatchResult add(boolean home) {
    return add(new MatchResult(home ? 1 : 0, home ? 0 : 1));
  }

  public Boolean getPlayed() {
    return played != null ? played : (homeScore + guestScore == maxGames);
  }

  public MatchResult add(MatchResult matchResult) {
    final Integer maxGames1 = matchResult.getMaxGames();
    final int maxGames = Math.max((maxGames1 == null ? 0 : maxGames1), this.maxGames == null ? 0 : this.maxGames);
    final int newHome = homeScore + matchResult.homeScore;
    final int newGuest = guestScore + matchResult.guestScore;
    return new MatchResult(newHome, newGuest, maxGames == 0 ? null : maxGames, newHome + newGuest >= maxGames || played);
  }

  public Boolean wasAcurate(Match match) {
    return played ? match.getResult().equals(determineExpectedResult(match)) : null;
  }

  public MatchResult expectResultOf(Match match) {
    return played ? match.getResult() : determineExpectedResult(match);
  }

  public MatchResult ofTeam(@NonNull Match match, @NonNull Team team) {
    if (match.getParticipator(team) == null) return null;
    if (match.getHome().getTeam().equals(team)) return this;
    return new MatchResult(guestScore, homeScore, maxGames, played);
  }

  //TODO (Abgie) 09.05.2023: WHY
  public double getGamePercentage(Match match) {
    return getPlayed() ? homeScore + guestScore == 0 ? -1. : (homeScore * 1. / (homeScore + guestScore)) : determineGamePercentage(match);
  }

  public MatchResult determineExpectedResult(Match match) {
    final double gamePercentage = determineGamePercentage(match);
    if (gamePercentage == -1) return new MatchResult(0, 0, maxGames, true);

    if (maxGames == null) {
      new DevInfo().error(new NullPointerException("Maxgames darf nicht null sein"));
      return new MatchResult(0, 0, 0, false);
    }

    if (maxGames % 2 == 0) {
      final int remaining = maxGames - homeScore - guestScore;
      final int score1 = (int) Math.round(gamePercentage * remaining);
      final int score2 = remaining - score1;
      return add(new MatchResult(score1, score2));
    }

    int endAt = (int) Math.ceil(maxGames / 2.);
    double score1 = homeScore;
    double score2 = guestScore;
    while (Math.round(Math.max(score1, score2)) < endAt) {
      score1 += gamePercentage;
      score2 += 1 - gamePercentage;
    }
    return new MatchResult((int) Math.round(score1), (int) Math.round(score2), maxGames, played);
  }

  //TODO (Abgie) 09.05.2023: WHY
  public String getWinPercent(Match match, boolean isHome) {
    double gamePercentage = determineGamePercentage(match);
    if (!isHome) gamePercentage = 1 - gamePercentage;
    return Math.round(1000 * (1 - gamePercentage) / 10) + "%";
  }

  /**
   * @return Wert zwischen 0 und 1 <p> Wie hoch die Chance, dass home ein Game gewinnt
   */
  private double determineGamePercentage(Match match) {
    final Integer homeMMR = Util.avoidNull(match.getHome(), 0, participator -> participator.getTeamLineup().getMmr());
    final Integer guestMMR = Util.avoidNull(match.getGuest(), 0, participator -> participator.getTeamLineup().getMmr());

    final double percentage = (homeMMR + guestMMR == 0) ? 0 : homeMMR * 1. / (guestMMR + homeMMR);
    if (percentage < .5 - Const.PREDICTION_FACTOR) return percentage / (2 - Const.PREDICTION_FACTOR * 4);
    if (percentage > .5 + Const.PREDICTION_FACTOR) return 1 - (1 - percentage) / (2 - Const.PREDICTION_FACTOR * 4);
    return (percentage - (.5 - Const.PREDICTION_FACTOR)) / (Const.PREDICTION_FACTOR * 4) + .25;
  }

  /**
   * @return Wie es angezeigt werden soll
   */
  @Override
  public String toString() {
    return getPlayed() ? homeScore + ":" + guestScore : "-:-";
  }

  public String currentResultString() {
    final String s = homeScore + ":" + guestScore;
    return getPlayed() ? s : "(" + s + ")";
  }

  @Override
  public int compareTo(@NotNull MatchResult o) {
    return Comparator.comparing(MatchResult::getHomeScore).compare(o, this);
  }
}
