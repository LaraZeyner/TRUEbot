package de.zahrie.trues.api.riot.matchhistory.teamperformance;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
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
  @JoinColumn(name = "team")
  @ToString.Exclude
  private Team team;

  @OneToMany(mappedBy = "teamPerformance")
  @ToString.Exclude
  private Set<Performance> performances = new LinkedHashSet<>();

  public TeamPerf(Game game, boolean first, boolean win, Integer totalKills, Integer totalDeaths, Integer totalAssists, Integer totalGold, Integer totalDamage, Integer totalVision, Integer totalCreeps, short turrets, short drakes, short inhibs, short heralds, short barons) {
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
  }

  public TeamPerf getOpponent() {
    return game.getTeamPerformances().stream().filter(teamPerf -> !teamPerf.equals(this)).findFirst().orElse(null);
  }

  public Team getOpposingTeam() {
    return Util.avoidNull(getOpponent(), null, TeamPerf::getTeam);
  }

  public String getWinString() {
    return win ? "Gewonnen" : "Verloren";
  }
}
