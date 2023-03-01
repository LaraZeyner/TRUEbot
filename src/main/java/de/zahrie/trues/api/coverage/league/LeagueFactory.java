package de.zahrie.trues.api.coverage.league;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.season.PrimeSeason;
import de.zahrie.trues.util.database.Database;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public final class LeagueFactory {

  public static League getGroup(PrimeSeason season, String divisionName, int stageId) {
    League league =  Database.Find.find(League.class, new String[]{"name", "season"}, new Object[]{divisionName, season}, "fromNameAndSeason");
    if (league == null) {
      league = new League();
      league.setName(divisionName);
      league.setStage(season.getStageOfId(stageId));
      Database.save(league);
    }
    return league;
  }

}
