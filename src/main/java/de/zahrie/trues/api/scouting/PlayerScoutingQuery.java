package de.zahrie.trues.api.scouting;

import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.util.Util;

public class PlayerScoutingQuery<T> extends TeamScoutingQuery<T> {
  private final Player player;

  PlayerScoutingQuery(Class<T> clazz, ScoutingGameType gameType, int days, Player player) {
    super(clazz, gameType, days, player.getTeam());
    this.player = player;
  }

  @Override
  public List<T> performance(String selectedColumns, String suffix) {
    final String query = "SELECT " + selectedColumns + " FROM Performance p WHERE player = " + player + " AND teamPerformance.game.start > " + start + " AND " + gameTypeString() + Util.avoidNull(suffix, "", str -> " " + str);
    return QueryBuilder.hql(clazz, query).list();
  }

  @Override
  public List<T> selection(String selectedColumns, String suffix) {
    final String query = "SELECT " + selectedColumns + " FROM Selection s WHERE game IN (SELECT distinct teamPerformance.game FROM Performance p WHERE player = " + player + " AND teamPerformance.game.start > " + start + " AND " + gameTypeString() + ")" + Util.avoidNull(suffix, "", str -> " " + str);
    return QueryBuilder.hql(clazz, query).list();
  }

  @Override
  protected String gameTypeString() {
    return switch (gameType) {
      case PRM_ONLY -> "teamPerformance.game.type <= 1";
      case PRM_CLASH -> "teamPerformance.game.type <= 2";
      case TEAM_GAMES -> "(teamPerformance IN (FROM TeamPerf WHERE team = " + team + " AND teamPerformance.game.type <= 3) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player = " + player + " AND teamPerformance.game.type <= 2 GROUP BY teamPerformance ORDER BY count(p2) DESC))";
      case MATCHMADE -> "";
    };
  }
}
