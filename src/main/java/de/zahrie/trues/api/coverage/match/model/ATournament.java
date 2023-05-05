package de.zahrie.trues.api.coverage.match.model;

import de.zahrie.trues.api.coverage.league.model.LeagueBase;

public interface ATournament {
  LeagueBase getLeague(); // league
  int getMatchIndex(); // match_index
  int getMatchId(); // match_id
}
