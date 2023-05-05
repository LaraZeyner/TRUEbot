package de.zahrie.trues.api.coverage.league;

import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.stage.model.Stage;

public final class LeagueFactory {
  public static PRMLeague getGroup(PRMSeason season, String divisionName, int stageId, int divisionId) {
    final Stage stage = season.getStage(stageId);
    return new PRMLeague(divisionId, stage, divisionName).create();
  }
}
