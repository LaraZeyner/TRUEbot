package de.zahrie.trues.api.riot.matchhistory.performance;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.util.Util;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import org.hibernate.annotations.NamedQuery;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "performance", indexes = @Index(name = "performance_idx_t_perf_champion", columnList = "t_perf, champion"))
@NamedQuery(name = "Performance.prmOfPlayer",
    query = "SELECT teamPerformance.game.start || ' - ' || lane, champion.name || ' vs ' || opponent.name, kda.kills || kda.deaths || kda.assists FROM Performance WHERE teamPerformance.game.type = :gameType AND player.summonerName = :player ORDER BY teamPerformance.game.start DESC LIMIT 10")
public class Performance implements Serializable, Comparable<Performance> {
  @Serial
  private static final long serialVersionUID = -6819821644121738377L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "perf_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
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

  @Embedded
  private KDA kda;


  @Column(name = "gold", nullable = false)
  private int gold;

  @Column(name = "damage")
  private Integer damage;

  @Column(name = "vision", columnDefinition = "SMALLINT UNSIGNED")
  private Integer vision;

  @Column(name = "creeps", columnDefinition = "SMALLINT UNSIGNED not null")
  private int creeps;

  public Performance(Player player, Lane lane, Champion champion, Champion opponent, KDA kda, int gold, Integer damage, Integer vision, int creeps) {
    this.player = player;
    this.lane = lane;
    this.champion = champion;
    this.opponent = opponent;
    this.kda = kda;
    this.gold = gold;
    this.damage = damage;
    this.vision = vision;
    this.creeps = creeps;
  }

  public String getPlayername() {
    return player.getSummonerName();
  }

  public String getMatchup() {
    return champion.getName() + " vs " + Util.avoidNull(opponent, "no data", Champion::getName);
  }

  public String getStats() {
    return kda.toString() + "(" + (lane.equals(Lane.UTILITY) ? vision : creeps) + ")";
  }

  @Override
  public int compareTo(@NotNull Performance o) {
    return Comparator.comparing(Performance::getLane).compare(this, o);
  }

}
