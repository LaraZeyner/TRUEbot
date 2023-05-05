package de.zahrie.trues.api.coverage.team.leagueteam;

import java.io.Serial;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.league.model.LeagueBase;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.model.LeagueMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.coverage.team.model.TeamScore;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@Setter
@Table("league_team")
public class LeagueTeam implements Entity<LeagueTeam>, Comparable<LeagueTeam> {
  @Serial
  private static final long serialVersionUID = -2748540818479532130L;
  private int id; // league_team_id
  private final LeagueBase league; // league
  private final TeamBase team; // team
  private final TeamScore score; // current_place, current_wins, current_losses

  public LeagueTeam(LeagueBase league, TeamBase team, TeamScore score) {
    this.league = league;
    this.team = team;
    this.score = score;
  }

  public static LeagueTeam get(Object[] objects) {
    final LeagueTeam leagueTeam = new LeagueTeam(
        (int) objects[0],
        new Query<League>().entity(objects[1]),
        new Query<TeamBase>().entity(objects[2]),
        new TeamScore((short) objects[3], (short) objects[4], (short) objects[5])
    );
    leagueTeam.getLeague().getLeagueTeams().add(leagueTeam);
    return leagueTeam;
  }

  @Override
  public LeagueTeam create() {
    return new Query<LeagueTeam>().key("league", league).key("team", team)
        .col("current_place", score.place()).col("current_wins", score.wins()).col("current_losses", score.losses()).insert(this, leagueTeam -> league.getLeagueTeams().add(this));
  }

  @Override
  public int compareTo(@NotNull LeagueTeam o) {
    return Comparator.comparing(LeagueTeam::getLeague)
        .thenComparing(LeagueTeam::getScore).compare(this, o);
  }

  @Override
  public String toString() {
    return league.getName() + " - " + score.toString();
  }

  public TeamScore getExpectedScore() {
    final Map<TeamBase, MatchResult> results = new TreeMap<>();
    for (final LeagueMatch match : league.getMatches()) {
      for (final Participator participator : match.getParticipators()) {
        final TeamBase participatingTeam = participator.getTeam();
        if (participatingTeam == null) continue;

        final MatchResult resultHandler = results.containsKey(participatingTeam) ? results.get(participatingTeam) :
            new MatchResult(0, 0);
        final MatchResult resultHandler2 = match.getResult().ofTeam(match, participatingTeam);
        if (resultHandler2 != null) results.put(participatingTeam, resultHandler.add(resultHandler2));
      }
    }
    final MatchResult resultHandler = results.get(team);
    return new TeamScore((short) results.keySet().stream().toList().indexOf(team), (short) resultHandler.getHomeScore(), (short) resultHandler.getGuestScore());
  }
}
