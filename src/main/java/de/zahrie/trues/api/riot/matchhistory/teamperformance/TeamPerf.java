package de.zahrie.trues.api.riot.matchhistory.teamperformance;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.util.Util;
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
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "team_perf", indexes = {@Index(name = "team_perf_idx_win_game", columnList = "win, game")})
@NamedQuery(name = "TeamPerf.fromGameAndSide", query = "FROM TeamPerf WHERE game = :game AND first = :first")
@NamedQuery(name = "TeamPerf.getGamesPRM", query = "SELECT teamPerformance FROM Performance WHERE player.team = :team AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney' ORDER BY teamPerformance.game.start desc")
@NamedQuery(name = "TeamPerf.getGamesPRMClash", query = "SELECT teamPerformance FROM Performance WHERE player.team = :team AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') ORDER BY teamPerformance.game.start desc")
@NamedQuery(name = "TeamPerf.getGamesTeamGames", query = "SELECT teamPerformance FROM Performance WHERE player.team = :team AND (teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance, player.team HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player = :player AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC)) ORDER BY teamPerformance.game.start desc")
@NamedQuery(name = "TeamPerf.getGamesMatchmade", query = "SELECT teamPerformance FROM Performance WHERE player = :player AND teamPerformance.game.start > :start ORDER BY teamPerformance.game.start desc")
@NamedQuery(name = "TeamPerf.getPresencePRM", query = "SELECT champion, count(s) FROM Selection s WHERE game IN (SELECT teamPerformance FROM Performance p WHERE player.team = :team AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney' GROUP BY lane ORDER BY count(p)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "TeamPerf.getPresencePRMClash", query = "SELECT champion, count(s) FROM Selection s WHERE game IN (SELECT teamPerformance FROM Performance p WHERE player.team = :team AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') GROUP BY lane ORDER BY count(p)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "TeamPerf.getPresenceTeamGames", query = "SELECT champion, count(s) FROM Selection s WHERE (game IN (SELECT teamPerformance.game FROM Performance p1 WHERE player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR game IN (SELECT teamPerformance.game FROM Performance p2 WHERE player.team = :team AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "TeamPerf.getPresenceMatchmade", query = "SELECT champion, count(s) FROM Selection s WHERE game IN (SELECT teamPerformance.game FROM Performance p WHERE player.team = :team AND teamPerformance.game.start > :start GROUP BY lane ORDER BY count(p)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "TeamPerf.getStatsPRM", query = "SELECT champion, count(p), sum(if(teamPerformance.win, 1, 0)), sum(kda.kills), sum(kda.deaths), sum(kda.assists) FROM Performance p WHERE teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team = :team AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney' GROUP BY lane ORDER BY count(p1)) GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "TeamPerf.getStatsPRMClash", query = "SELECT champion, count(p), sum(if(teamPerformance.win, 1, 0)), sum(kda.kills), sum(kda.deaths), sum(kda.assists) FROM Performance p WHERE teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team = :team AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') GROUP BY lane ORDER BY count(p1)) GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "TeamPerf.getStatsTeamGames", query = "SELECT champion, count(p), sum(if(teamPerformance.win, 1, 0)), sum(kda.kills), sum(kda.deaths), sum(kda.assists) FROM Performance p WHERE teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player.team = :team AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC) GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "TeamPerf.getStatsMatchmade", query = "SELECT champion, count(p), sum(if(teamPerformance.win, 1, 0)), sum(kda.kills), sum(kda.deaths), sum(kda.assists) FROM Performance p WHERE teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team = :team AND teamPerformance.game.start > :start GROUP BY lane ORDER BY count(p1)) GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "TeamPerf.getGamesPRM", query = "SELECT count(distinct teamPerformance.game) FROM Performance p WHERE player.team = :team AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney'")
@NamedQuery(name = "TeamPerf.getGamesPRMClash", query = "SELECT count(distinct teamPerformance.game) FROM Performance p WHERE player.team = :team AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash')")
@NamedQuery(name = "TeamPerf.getGamesTeamGames",  query = "SELECT count(distinct teamPerformance.game) FROM Performance p WHERE teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player.team = :team AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC) GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "TeamPerf.getGameseMatchmade", query = "SELECT champion, count(s) FROM Selection s WHERE game IN (SELECT teamPerformance.game FROM Performance p WHERE player.team = :team AND teamPerformance.game.start > :start GROUP BY lane ORDER BY count(p)) GROUP BY champion ORDER BY count(s) desc")
public class TeamPerf implements Serializable {
  @Serial
  private static final long serialVersionUID = 2781415098543369302L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "t_perf_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "game", nullable = false)
  @ToString.Exclude
  private Game game;

  @Column(name = "first", nullable = false)
  private boolean first = false;

  @Column(name = "win", nullable = false)
  private boolean win = false;

  @Embedded
  private KDA kda;

  @Column(name = "total_gold")
  private Integer totalGold;

  @Column(name = "total_damage")
  private Integer totalDamage;

  @Column(name = "total_vision", columnDefinition = "SMALLINT UNSIGNED")
  private Integer totalVision;

  @Column(name = "total_creeps", columnDefinition = "SMALLINT UNSIGNED")
  private Integer totalCreeps;

  @Column(name = "turrets", columnDefinition = "TINYINT UNSIGNED not null")
  private short turrets;

  @Column(name = "drakes", columnDefinition = "TINYINT UNSIGNED not null")
  private short drakes;

  @Column(name = "inhibs", columnDefinition = "TINYINT UNSIGNED not null")
  private short inhibs;

  @Column(name = "heralds", columnDefinition = "TINYINT UNSIGNED not null")
  private short heralds;

  @Column(name = "barons", columnDefinition = "TINYINT UNSIGNED not null")
  private short barons;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "prm_team")
  @ToString.Exclude
  private PrimeTeam prmTeam;

  @OneToMany(mappedBy = "teamPerformance")
  @ToString.Exclude
  private Set<Performance> performances = new LinkedHashSet<>();

  public TeamPerf(Game game, boolean first, boolean win, Integer totalKills, Integer totalDeaths, Integer totalAssists, Integer totalGold, Integer totalDamage, Integer totalVision, Integer totalCreeps, short turrets, short drakes, short inhibs, short heralds, short barons) {
    this(game, first, win, totalKills, totalDeaths, totalAssists, totalGold, totalDamage, totalVision, totalCreeps, turrets, drakes, inhibs, heralds, barons, null);
  }

  public TeamPerf(Game game, boolean first, boolean win, Integer totalKills, Integer totalDeaths, Integer totalAssists, Integer totalGold, Integer totalDamage, Integer totalVision, Integer totalCreeps, short turrets, short drakes, short inhibs, short heralds, short barons, PrimeTeam prmTeam) {
    this.game = game;
    this.first = first;
    this.win = win;
    this.kda = new KDA(totalKills.shortValue(), totalDeaths.shortValue(), totalAssists.shortValue());
    this.totalGold = totalGold;
    this.totalDamage = totalDamage;
    this.totalVision = totalVision;
    this.totalCreeps = totalCreeps;
    this.turrets = turrets;
    this.drakes = drakes;
    this.inhibs = inhibs;
    this.heralds = heralds;
    this.barons = barons;
    this.prmTeam = prmTeam;
  }

  public TeamPerf getOpponent() {
    return game.getTeamPerformances().stream().filter(teamPerf -> !teamPerf.equals(this)).findFirst().orElse(null);
  }

  public PrimeTeam getOpposingTeam() {
    return Util.avoidNull(getOpponent(), null, TeamPerf::getPrmTeam);
  }

  public String getWinString() {
    return win ? "Gewonnen" : "Verloren";
  }
}
