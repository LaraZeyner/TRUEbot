package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.DTO;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.util.Util;

public record MatchOfTeamDTO(Match match, Team team) implements DTO {
  public static QueryBuilder<MatchOfTeamDTO> get(Team team) {
    return get(team, LocalDateTime.now().minusDays(180));
  }

  public static QueryBuilder<MatchOfTeamDTO> get(Team team, LocalDateTime start) {
    return QueryBuilder.hql(MatchOfTeamDTO.class,
        "SELECT coverage, team " +
            "FROM Participator " +
            "WHERE team = " + team + " " +
            "and (coverage.start >= " + start + " or coverage.status <>" + EventStatus.PLAYED + ") " +
            "ORDER BY coverage.start");
  }

  @Override
  public List<String> getData() {
    return List.of(
        TimeFormat.DISCORD.of(match.getStart()),
        Util.avoidNull(match.getOpponentOf(team), "keine Gegner", Team::getName),
        match.getResult()
    );
  }
}
