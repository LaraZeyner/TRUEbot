package de.zahrie.trues.api.coverage.lineup;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.Match;

public class LineupManager {
  private static final List<MatchLineups> lineups = new ArrayList<>();

  private static MatchLineups addMatch(Match match) {
    final MatchLineups matchLineups = new MatchLineups(match);
    lineups.add(matchLineups);
    return matchLineups;
  }

  public static MatchLineups getMatch(Match match) {
    return lineups.stream().filter(lineup -> lineup.getMatch().equals(match)).findFirst().orElse(addMatch(match));
  }

}
