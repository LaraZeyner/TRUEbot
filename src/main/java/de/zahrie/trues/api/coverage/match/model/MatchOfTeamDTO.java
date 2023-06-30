package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.DTO;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.NotNull;

public record MatchOfTeamDTO(Match match, Team team) implements DTO<MatchOfTeamDTO> {
  public static Query<Participator> get(Team team) {
    return get(team, LocalDateTime.now().minusDays(180));
  }

  public static Query<Participator> get(Team team, LocalDateTime start) {
    return new Query<>(Participator.class).get("coverage", Match.class).get("team", Team.class)
        .join(new JoinQuery<>(Participator.class, Match.class).col("coverage"))
        .keep("team", team)
        .where(Condition.Comparer.GREATER_EQUAL, "_match.coverage_start", start).or("_match.status", EventStatus.PLAYED)
        .ascending("_match.coverage_start");
  }

  @Override
  public List<Object> getData() {
    return List.of(
        TimeFormat.DISCORD.of(match.getStart()),
        Util.avoidNull(match.getOpponentOf(team), "keine Gegner", Team::getName),
        match.getResult().toString()
    );
  }

  @Override
  public int compareTo(@NotNull MatchOfTeamDTO o) {
    return Comparator.comparing(MatchOfTeamDTO::match).compare(this, o);
  }
}
