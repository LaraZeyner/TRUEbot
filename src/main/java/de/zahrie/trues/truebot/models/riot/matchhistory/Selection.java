package de.zahrie.trues.truebot.models.riot.matchhistory;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.truebot.models.riot.Champion;
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
@Table(name = "selection",
        indexes = { @Index(name = "selection_idx_game_champion", columnList = "game, champion") })
public class Selection implements Serializable {
  @Serial
  private static final long serialVersionUID = 987288043790995351L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "draft_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "game", nullable = false)
  @ToString.Exclude
  private Game game;

  @Column(name = "first", nullable = false)
  private boolean isFirstPick = false;

  @Column(name = "select_order", nullable = false)
  private byte selectOrder;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 4)
  private SelectionType type;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion", nullable = false)
  @ToString.Exclude
  private Champion champion;

}