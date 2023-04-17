package de.zahrie.trues.api.coverage.team.leagueteam;

import java.util.Map;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.coverage.team.model.TeamScore;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import lombok.NonNull;

public class LeagueTeamFactory {
  @NonNull
  public static LeagueTeam create(League league, Team team, TeamScore score) {
    LeagueTeam leagueTeam = QueryBuilder.hql(LeagueTeam.class, "FROM LeagueTeam WHERE league = :league and team = :team")
        .addParameters(Map.of("league", league, "team", team)).single();
    if (leagueTeam == null) leagueTeam = LeagueTeam.build(league, team);
    leagueTeam.setScore(score);
    Database.update(leagueTeam);
    return leagueTeam;
  }
}
