package de.zahrie.trues.api.coverage.league;

import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.database.QueryBuilder;

public final class LeagueFactory {
  public static PRMLeague getGroup(PRMSeason season, String divisionName, int stageId, int divisionId) {
    PRMLeague league = QueryBuilder.hql(PRMLeague.class, "FROM PRMLeague WHERE name = " + divisionName + " AND stage.season = " + season.getId()).single();
    if (league == null) {
      final PlayStage stage = season.getStage(stageId);
      league = PRMLeague.build(divisionName, stage, divisionId);
    }
    return league;
  }

}
