package de.zahrie.trues.api.coverage.match.model;

import de.zahrie.trues.api.coverage.league.model.League;

public interface ATournament {
  League getLeague(); // league
  int getMatchIndex(); // match_index
  int getMatchId(); // match_id
}
