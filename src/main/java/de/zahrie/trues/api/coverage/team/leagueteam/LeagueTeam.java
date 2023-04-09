package de.zahrie.trues.api.coverage.team.leagueteam;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.MatchResultHandler;
import de.zahrie.trues.api.coverage.match.model.TournamentMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.coverage.team.model.TeamScore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "league_team", indexes = @Index(name = "league_team_idx_league_team", columnList = "league, team", unique = true))
public class LeagueTeam implements Serializable, Comparable<LeagueTeam> {
  @Serial
  private static final long serialVersionUID = -763378764697829834L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "leagueteam_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "league", nullable = false)
  @ToString.Exclude
  private League league;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "team", nullable = false)
  @ToString.Exclude
  private Team team;

  @Embedded
  private TeamScore score;

  @Override
  public int compareTo(@NotNull LeagueTeam o) {
    return Comparator.comparing(LeagueTeam::getLeague).compare(this, o);
  }

  @Override
  public String toString() {
    return league.getName() + " - " + score.toString();
  }

  public TeamScore getExpectedScore() {
    final Map<Team, MatchResultHandler> results = new HashMap<>();
    for (final TournamentMatch match : league.getMatches()) {
      for (final Participator participator : match.getParticipators()) {
        final Team participatingTeam = participator.getTeam();
        if (participatingTeam == null) continue;
        final MatchResultHandler resultHandler = results.containsKey(participatingTeam) ? results.get(participatingTeam) : new MatchResultHandler((short) 0, (short) 0, true);
        final MatchResultHandler resultHandler2 = match.getResultHandler().ofTeam(match, participatingTeam);
        results.put(participatingTeam, resultHandler.add(resultHandler2));
      }
    }
    final MatchResultHandler resultHandler = results.get(team);
    final TreeMap<Team, MatchResultHandler> sorted = new TreeMap<>(results);
    return new TeamScore(league, (short) sorted.keySet().stream().toList().indexOf(team), resultHandler.getHomeScore(), resultHandler.getGuestScore());
  }
}
