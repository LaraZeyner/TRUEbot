package de.zahrie.trues.truebot.models.riot.matchhistory;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.truebot.models.riot.Champion;
import de.zahrie.trues.truebot.models.riot.Lane;
import de.zahrie.trues.truebot.models.riot.Player;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "performance", indexes = {
        @Index(name = "performance_idx_t_perf_champion", columnList = "t_perf, champion") })
public class Performance implements Serializable {
  @Serial
  private static final long serialVersionUID = -6819821644121738377L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "perf_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "t_perf", nullable = false)
  @ToString.Exclude
  private TeamPerf teamPerformance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player")
  @ToString.Exclude
  private Player player;

  @Enumerated(EnumType.STRING)
  @Column(name = "lane", length = 7)
  private Lane lane;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion", nullable = false)
  @ToString.Exclude
  private Champion champion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "enemy_champion")
  @ToString.Exclude
  private Champion opponent;

  @Column(name = "kills", columnDefinition = "TINYINT UNSIGNED not null")
  private short kills;

  @Column(name = "deaths", columnDefinition = "TINYINT UNSIGNED not null")
  private short deaths;

  @Column(name = "assists", columnDefinition = "TINYINT UNSIGNED not null")
  private short assists;

  @Column(name = "gold", nullable = false)
  private int gold;

  @Column(name = "damage")
  private Integer damage;

  @Column(name = "vision", columnDefinition = "SMALLINT UNSIGNED")
  private Integer vision;

  @Column(name = "creeps", columnDefinition = "SMALLINT UNSIGNED not null")
  private int creeps;

}