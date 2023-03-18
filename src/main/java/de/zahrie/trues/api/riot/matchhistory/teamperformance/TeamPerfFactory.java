package de.zahrie.trues.api.riot.matchhistory.teamperformance;

import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.database.Database;

public class TeamPerfFactory {
  public static TeamPerf getTeamPerfBySide(Game game, boolean blueSide) {
    return Database.Find.find(TeamPerf.class, new String[]{"game", "first"}, new Object[]{game, blueSide}, "fromGameAndSide");
  }
}
