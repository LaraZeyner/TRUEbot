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
        .get(" - ", Formatter.of("_game.start_time", Formatter.CellFormat.AUTO), Formatter.of("lane"))
        .get(" vs ", Formatter.of("_my.champion_name"), Formatter.of("_enemy.champion_name"))
        .get(" / ", Formatter.of("kills"), Formatter.of("deaths"), Formatter.of("assists"))
        .where("_game.game_type", gameType).and("player", player)
        .descending("_game.start_time").list();
  }
}
