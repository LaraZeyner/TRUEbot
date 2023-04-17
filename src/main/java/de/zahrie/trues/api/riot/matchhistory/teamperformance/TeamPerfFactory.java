package de.zahrie.trues.api.riot.matchhistory.teamperformance;

import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.riot.matchhistory.game.Game;

public class TeamPerfFactory {
  public static TeamPerf getTeamPerfBySide(Game game, boolean blueSide) {
    return QueryBuilder.hql(TeamPerf.class, "FROM TeamPerf WHERE game = " + game.getId() + " and first = " + blueSide).single();
  }
}
