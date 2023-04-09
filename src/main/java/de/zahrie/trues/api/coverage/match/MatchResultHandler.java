package de.zahrie.trues.api.coverage.match;

import java.util.Comparator;

import de.zahrie.trues.api.coverage.lineup.LineupManager;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.Util;
import lombok.Data;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@Data
@ExtensionMethod(StringUtils.class)
public final class MatchResultHandler implements Comparable<MatchResultHandler> {
  private final short homeScore;
  private final short guestScore;
  private final boolean played;

  public MatchResultHandler add(MatchResultHandler matchResultHandler) {
    return new MatchResultHandler((short) (homeScore + matchResultHandler.homeScore), (short) (guestScore + matchResultHandler.guestScore), played);
  }

  public Boolean wasAcurate(Match match) {
    return played ? match.getResult().equals(determineExpectedResult(match).toString()) : null;
  }

  public MatchResultHandler expectResultOf(Match match) {
    return played ? fromResultString(match.getResult()) : determineExpectedResult(match);
  }

  public MatchResultHandler ofTeam(Match match, Team team) {
    if (team == null) return new MatchResultHandler((short) 0, (short) 0, true);
    if (Util.avoidNull(match.getHome(), null, Participator::getTeam).equals(team)) {
      return this;
    }
    return new MatchResultHandler(homeScore, guestScore, true);
  }

  public double getGamePercentage(Match match) {
    if (played) {
      return switch (match.getResult()) {
        case "2:0", "1:0" -> 1.0;
        case "1:1" -> .5;
        case "0:2", "0:1" -> 0.0;
        default -> -1.0;
      };
    }
    return determineGamePercentage(match);
  }

  public MatchResultHandler determineExpectedResult(Match match) {
    final double gamePercentage = determineGamePercentage(match);
    if (gamePercentage == -1) return new MatchResultHandler((short) 0, (short) 0, true);
    if (gamePercentage < .25) return new MatchResultHandler((short) 0, (short) 2, true);
    if (gamePercentage > .75) return new MatchResultHandler((short) 2, (short) 0, true);
    return new MatchResultHandler((short) 1, (short) 1, true);
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

  public static MatchResultHandler fromResultString(String resultString) {
    if (resultString.equals("-:-")) {
      return new MatchResultHandler((short) 0, (short) 0, false);
    } else if (resultString.matches("\\d+:\\d+")) {
      return new MatchResultHandler(resultString.between(null, ":").intValue().shortValue(), resultString.between(":").intValue().shortValue(), true);
    } else {
      throw new IllegalArgumentException("Das Ergebnis ist nicht gültig!");
    }
  }

  public void update(Match match) {
    final Participator home = match.getHome();
    if (home != null) {
      home.setWins(homeScore);
      Database.save(home);
    }

    final Participator guest = match.getGuest();
    if (guest != null) {
      guest.setWins(guestScore);
      Database.save(guest);
    }

    match.setResult(toString());
    Database.save(match);
  }

  @Override
  public String toString() {
    return played ? homeScore + ":" + guestScore : "-:-";
  }

  @Override
  public int compareTo(@NotNull MatchResultHandler o) {
    return Comparator.comparing(MatchResultHandler::getHomeScore).compare(o, this);
  }
}
