package de.zahrie.trues.api.coverage.league;

import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;

public final class LeagueFactory {
  public static PRMLeague getGroup(PRMSeason season, String divisionName, int stageId) {
    PRMLeague league = QueryBuilder.hql(PRMLeague.class, "FROM PRMLeague WHERE name = " + divisionName + " AND stage.season = " + season).single();
    if (league == null) {
      league = new PRMLeague();
      league.setName(divisionName);
      league.setStage(season.getStageOfId(stageId));
      Database.save(league);
    }
    return league;
  }

}
