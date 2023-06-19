package de.zahrie.trues.api.coverage.team.leagueteam;

import java.io.Serial;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.model.LeagueMatch;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.coverage.team.model.TeamScore;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@Setter
@Table("league_team")
@ExtensionMethod(SQLUtils.class)
public class LeagueTeam implements Entity<LeagueTeam>, Comparable<LeagueTeam> {
  @Serial
  private static final long serialVersionUID = -2748540818479532130L;
  private int id; // league_team_id
  private final League league; // league
  private final Team team; // team
  private final TeamScore score; // current_place, current_wins, current_losses

  public LeagueTeam(League league, Team team, @NonNull TeamScore score) {
    this.league = league;
    this.team = team;
    this.score = score;
  }

  public static LeagueTeam get(List<Object> objects) {
    return new LeagueTeam(
        (int) objects.get(0),
        new Query<>(League.class).entity(objects.get(1)),
        new Query<>(Team.class).entity(objects.get(2)),
        new TeamScore(objects.get(3).shortValue(), objects.get(4).shortValue(), objects.get(5).shortValue())
    );
  }

  @Override
  public LeagueTeam create() {
    return new Query<>(LeagueTeam.class).key("league", league).key("team", team)
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
    final Map<Team, MatchResult> results = new TreeMap<>();
    for (final LeagueMatch match : league.getMatches()) {
      for (final Participator participator : match.getParticipators()) {
        final Team participatingTeam = participator.getTeam();
        if (participatingTeam == null) continue;

        final MatchResult resultHandler = results.containsKey(participatingTeam) ? results.get(participatingTeam) :
            new MatchResult(match, 0, 0);
        final MatchResult resultHandler2 = match.getResult().ofTeam(participatingTeam);
        if (resultHandler2 != null) results.put(participatingTeam, resultHandler.add(resultHandler2));
      }
    }
    final MatchResult resultHandler = results.get(team);
    return new TeamScore((short) results.keySet().stream().toList().indexOf(team), (short) resultHandler.getHomeScore(), (short) resultHandler.getGuestScore());
  }

  public LeagueTier getCurrentTier() {
    return league.getTier();
  }

  public LeagueTier getNext() {
    if (score.place() == 0) return LeagueTier.Swiss_Starter;
    if (league.getTier().equals(LeagueTier.Swiss_Starter)) {
      if (score.wins() < 6) return LeagueTier.Division_8;
      if (score.wins() < 9) return LeagueTier.Division_7;
      if (score.wins() < 11) return LeagueTier.Division_6;
      if (score.wins() < 13) return LeagueTier.Division_5;
      return LeagueTier.Division_4_Playoffs;
    }
    if (score.place() > 6) return LeagueTier.fromIndex(getCurrentTier().getIndex() + 1);
    if (score.place() < 3) return LeagueTier.fromIndex(getCurrentTier().getIndex() - 1);
    return getCurrentTier();
  }
}
