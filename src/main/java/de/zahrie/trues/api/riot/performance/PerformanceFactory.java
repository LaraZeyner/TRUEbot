package de.zahrie.trues.api.riot.performance;

import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.query.Formatter;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.api.riot.game.GameType;

public class PerformanceFactory {
  public static List<Object[]> getLastPlayerGames(GameType gameType, Player player) {
    return new Query<>(Performance.class, 10)
        .join(new JoinQuery<>(Performance.class, TeamPerf.class, "t_perf"))
        .join(new JoinQuery<>(TeamPerf.class, Game.class, "_teamperf.game"))
        .join(new JoinQuery<>(Performance.class, Champion.class, "_my"))
        .join(new JoinQuery<>(Performance.class, Champion.class, "enemy_champion", "enemy"))
        .get(" - ", Formatter.of("_game.start_time", Formatter.CellFormat.AUTO),
            Formatter.of("IF(lane = 1, 'Top', IF(lane = 2, 'Jgl', IF(lane = 3, 'Mid', IF(lane = 4, 'Bot', IF(lane = 5, 'Sup', 'none')))))"))
        .get(" vs ", Formatter.of("_my.champion_name"), Formatter.of("_enemy.champion_name"))
        .get(" / ", Formatter.of("kills"), Formatter.of("deaths"),
            Formatter.of("CONCAT(_performance.assists, ' (', IF(_teamperf.win = false, 'N', 'S'), ')')"))
        .where("_game.game_type", gameType).and("player", player)
        .descending("_game.start_time").list();
  }
}
