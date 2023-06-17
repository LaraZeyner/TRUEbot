package de.zahrie.trues.api.riot.performance;

import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.connector.DTO;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLGroup;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public record PlayerMatchHistoryPerformanceDTO(Performance performance) implements DTO {

  public static Query<Performance> get(Player player, ScoutingGameType gameType, @Nullable Lane lane, @Nullable Champion champion) {
    final var query = new Query<>(Performance.class)
        .join(new JoinQuery<>(Performance.class, TeamPerf.class).col("t_perf"))
        .join(new JoinQuery<>(TeamPerf.class, Game.class))
        .where("player", player)
        .descending("_game.start_time");
    if (lane != null) query.where("lane", lane);
    if (champion != null) query.where("champion", champion);
    switch (gameType) {
      case PRM_ONLY -> query.where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.CUSTOM);
      case PRM_CLASH -> query.where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.CLASH);
      case TEAM_GAMES -> query.join(new JoinQuery<>(Performance.class, Player.class))
          .where("_player.team", player.getTeam())
          .and(Condition.inSubquery("t_perf", new Query<>(Performance.class).distinct("t_perf", Integer.class)
                  .join(new JoinQuery<>(Performance.class, TeamPerf.class).col("t_perf"))
                  .join(new JoinQuery<>(TeamPerf.class, Game.class))
                  .where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.CLASH).and("player", player)
                  .include(new Query<>(Performance.class).distinct("t_perf", Integer.class)
                      .join(new JoinQuery<>(Performance.class, TeamPerf.class).col("t_perf"))
                      .join(new JoinQuery<>(TeamPerf.class, Game.class))
                      .join(new JoinQuery<>(Performance.class, Player.class))
                      .where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.RANKED_FLEX).and("player", player)
                      .groupBy(new SQLGroup("`t_perf`, `_player`.`team`").having("count(`performance_id`) > 2"))
                  )
              )
          );
    }
    return query;
  }

  @Override
  public List<String> getData() {
    return List.of(
        performance.getTeamPerformance().getGame().getType().name().charAt(0) + ": " +
            TimeFormat.DISCORD.of(performance.getTeamPerformance().getGame().getStart()),
        performance.getLane().toString().charAt(0) + ": " + performance.getMatchup().champion().getName() + " vs. " +
            Util.avoidNull(performance.getMatchup().opponent(), "kein Gegner", Champion::getName),
        (performance.getTeamPerformance().isWin() ? "W" : "L") + ": " + performance.getKda().toString()
    );
  }
}
