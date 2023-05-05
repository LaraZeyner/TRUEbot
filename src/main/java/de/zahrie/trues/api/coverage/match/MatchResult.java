package de.zahrie.trues.api.coverage.match;

import java.util.Comparator;

import de.zahrie.trues.api.coverage.lineup.LineupManager;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
@ExtensionMethod(StringUtils.class)
public final class MatchResult implements Comparable<MatchResult> {
  public static final MatchResult ZERO = new MatchResult(0, 0);

  private final int homeScore;
  private final int guestScore;
  private final Integer maxGames;
  private final Boolean played;

  public MatchResult(int homeScore, int guestScore) {
    this(homeScore, guestScore, null, null);
  }

  public MatchResult(int homeScore, int guestScore, int maxGames) {
    this(homeScore, guestScore, maxGames, null);
  }

  public Boolean getPlayed() {
    return played != null ? played : (homeScore + guestScore == maxGames);
  }

  public MatchResult add(MatchResult matchResult) {
    final Integer maxGames = (matchResult.getMaxGames() == null || this.maxGames == null) ? null : this.maxGames + matchResult.getMaxGames();
    return new MatchResult(homeScore + matchResult.homeScore, guestScore + matchResult.guestScore, maxGames, played);
  }

  public Boolean wasAcurate(Match match) {
    return played ? match.getResult().equals(determineExpectedResult(match)) : null;
  }

  public MatchResult expectResultOf(Match match) {
    return played ? match.getResult() : determineExpectedResult(match);
  }

  public MatchResult ofTeam(Match match, TeamBase team) {
    if (match.getParticipator(team) == null) return null;
    if (match.getHome().getTeam().equals(team)) return this;
    return new MatchResult(guestScore, homeScore, maxGames, played);
  }

  public double getGamePercentage(Match match) {
    return getPlayed() ? homeScore + guestScore == 0 ? -1. : (homeScore * 1. / (homeScore + guestScore)) : determineGamePercentage(match);
  }

  public MatchResult determineExpectedResult(Match match) {
    final double gamePercentage = determineGamePercentage(match);
    if (gamePercentage == -1) return new MatchResult(0, 0, maxGames, true);
    if (maxGames == null) throw new NullPointerException("Maxgames darf nicht null sein");

    final int score1 = (int) Math.round(gamePercentage * maxGames);
    return new MatchResult(score1, maxGames - score1, maxGames, true);
  }

  public String getWinPercent(Match match, boolean isHome) {
    double gamePercentage = determineGamePercentage(match);
    if (!isHome) gamePercentage = 1 - gamePercentage;
    return Math.round(1000 * (1 - gamePercentage) / 10) + "%";
  }

  private double determineGamePercentage(Match match) {
    final Integer homeMMR = Util.avoidNull(match.getHome(), 0, participator -> LineupManager.getMatch(match).getLineup(participator).getAverageMMR());
    final Integer guestMMR = Util.avoidNull(match.getGuest(), 0, participator -> LineupManager.getMatch(match).getLineup(participator).getAverageMMR());

    final double percentage = (homeMMR + guestMMR == 0) ? 0 : homeMMR * 1. / (guestMMR + homeMMR);
    if (percentage < .5 - Const.PREDICTION_FACTOR) return percentage / (2 - Const.PREDICTION_FACTOR * 4);
    if (percentage > .5 + Const.PREDICTION_FACTOR) return 1 - (1 - percentage) / (2 - Const.PREDICTION_FACTOR * 4);
    return (percentage - (.5 - Const.PREDICTION_FACTOR)) / (Const.PREDICTION_FACTOR * 4) + .25;
  }

  public static MatchResult fromResultString(String resultString, MatchFormat format) {
    if (resultString.equals("-:-")) {
      return new MatchResult(0, 0, format.ordinal(), false);
    } else if (resultString.matches("\\d+:\\d+")) {
      return new MatchResult(resultString.before(":").intValue(), resultString.after(":").intValue(), format.ordinal(), true);
    } else {
      throw new IllegalArgumentException("Das Ergebnis ist nicht gültig!");
    }
  }

  @Override
  public String toString() {
    return getPlayed() ? homeScore + ":" + guestScore : "-:-";
  }

  @Override
  public int compareTo(@NotNull MatchResult o) {
    return Comparator.comparing(MatchResult::getHomeScore).compare(o, this);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof MatchResult result && toString().equals(result.toString());
  }
}
