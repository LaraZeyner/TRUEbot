package de.zahrie.trues.api.riot.matchhistory;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "team_perf",
        indexes = { @Index(name = "team_perf_idx_win_game", columnList = "win, game") })
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
  private boolean isFirst = false;

  @Column(name = "win", nullable = false)
  private boolean win = false;

  @Column(name = "total_kills", columnDefinition = "SMALLINT UNSIGNED")
  private Integer totalKills;

  @Column(name = "total_deaths", columnDefinition = "SMALLINT UNSIGNED")
  private Integer totalDeaths;

  @Column(name = "total_assists", columnDefinition = "SMALLINT UNSIGNED")
  private Integer totalAssists;

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

  @Column(name = "prm_team")
  private Integer prmTeam;

  @OneToMany(mappedBy = "teamPerformance")
  @ToString.Exclude
  private Set<Performance> performances = new LinkedHashSet<>();

}