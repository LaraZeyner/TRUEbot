package de.zahrie.trues.api.coverage.lineup.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import jakarta.persistence.CascadeType;
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

/**
 * Lineup wird submitted ({@code ordered} = Command | {@code not ordered} = matchlog)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_lineup", indexes = {@Index(name = "idx_coverage_lineup", columnList = "coverage_team, player", unique = true)})
public class Lineup implements Serializable {
  @Serial
  private static final long serialVersionUID = -4332018463378754999L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_lineup_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "coverage_team", nullable = false)
  @ToString.Exclude
  private Participator participator;

  @Column(name = "lineup_id")
  @Enumerated(EnumType.ORDINAL)
  private Lane lane = Lane.UNKNOWN;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "player", nullable = false)
  @ToString.Exclude
  private Player player;

  public Lineup(Participator participator, Lane lane, Player player) {
    this.participator = participator;
    this.lane = lane;
    this.player = player;
  }
}
