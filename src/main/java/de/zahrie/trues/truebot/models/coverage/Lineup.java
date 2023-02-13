package de.zahrie.trues.truebot.models.coverage;

import de.zahrie.trues.truebot.models.riot.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_lineup", indexes = {
        @Index(name = "idx_coverage_lineup", columnList = "coverage_team, player", unique = true) })
public class Lineup implements Serializable {

  @Serial
  private static final long serialVersionUID = -4332018463378754999L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_lineup_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "coverage_team", nullable = false)
  @ToString.Exclude
  private Participator participator;

  @Column(name = "lineup_id")
  private Byte lineupId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "player", nullable = false)
  @ToString.Exclude
  private Player player;

  @Column(name = "automatic", nullable = false)
  private boolean automatic = true;

}