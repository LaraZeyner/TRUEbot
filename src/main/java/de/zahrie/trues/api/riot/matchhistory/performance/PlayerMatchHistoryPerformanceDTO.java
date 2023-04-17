package de.zahrie.trues.api.riot.matchhistory.performance;

import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.DTO;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public record PlayerMatchHistoryPerformanceDTO(Performance performance) implements DTO {

  public static QueryBuilder<PlayerMatchHistoryPerformanceDTO> get(Player player, ScoutingGameType gameType, @Nullable Lane lane, @Nullable Champion champion) {
    String whereClause = "FROM performance WHERE ";
    if (lane != null) whereClause += " lane = " + lane;
    if (champion != null) whereClause += " champion " + champion.getId();
    whereClause += (lane == null && champion == null) ? " " : " AND";

    final String hqlStatementString = whereClause + switch (gameType) {
      case PRM_ONLY -> "teamPerformance.game.type <= 1";
      case PRM_CLASH -> "teamPerformance.game.type <= 2";
      case TEAM_GAMES -> "player.team = " + player.getTeam().getId() + " AND teamPerformance.game.type <= 4 GROUP BY teamPerformance, player.team HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player = " + player.getId() + " AND (teamPerformance.game.type <= 1) GROUP BY teamPerformance ORDER BY count(p2) DESC)) AND player = " + player.getId();
      case MATCHMADE -> "";
    };
    return QueryBuilder.hql(PlayerMatchHistoryPerformanceDTO.class, hqlStatementString);
  }

  @Override
  public List<String> getData() {
    return List.of(
        performance.getTeamPerformance().getGame().getType().name().charAt(1) + ": " + TimeFormat.DISCORD.of(performance.getTeamPerformance().getGame().getStart()),
        performance.getChampion().getName() + " vs. " + Util.avoidNull(performance.getOpponent(), "kein Gegner", Champion::getName),
        (performance.getTeamPerformance().isWin() ? "W" : "L") + ": " + performance.getKda().toString()
    );
  }
}
