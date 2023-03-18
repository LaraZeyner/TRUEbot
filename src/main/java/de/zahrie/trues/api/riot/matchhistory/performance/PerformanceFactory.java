package de.zahrie.trues.api.riot.matchhistory.performance;

import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.database.Database;

public class PerformanceFactory {
  public static List<Object[]> getLastPlayerGames(GameType gameType, Player player) {
    return Database.Find.findObjectList(new String[]{"gameType", "player"}, new Object[]{gameType, player}, "Performance.prmOfPlayer");
  }

  public static Performance getPerformanceByPlayerAndTeamPerformance(Player player, TeamPerf teamPerformance) {
    return Database.Find.find(Performance.class, new String[]{"player", "teamPerformance"}, new Object[]{player, teamPerformance}, "fromPlayerAndTPerf");
  }
}
