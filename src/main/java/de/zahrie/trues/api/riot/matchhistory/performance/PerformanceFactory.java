package de.zahrie.trues.api.riot.matchhistory.performance;

import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.api.database.Database;

public class PerformanceFactory {
  public static List<Object[]> getLastPlayerGames(GameType gameType, Player player) {
    return Database.Find.findObjectList(new String[]{"gameType", "player"}, new Object[]{gameType, player}, "Performance.prmOfPlayer");
  }

  public static Performance getPerformanceByPlayerAndTeamPerformance(Player player, TeamPerf teamPerformance) {
    return QueryBuilder.hql(Performance.class, "FROM Performance WHERE player = " + player.getId() + " AND teamPerformance = " + teamPerformance.getId()).single();
  }
}
