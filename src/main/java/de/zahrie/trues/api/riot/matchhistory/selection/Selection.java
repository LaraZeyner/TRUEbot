package de.zahrie.trues.api.riot.matchhistory.selection;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
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

  public Selection(Game game, boolean isFirstPick, byte selectOrder, SelectionType type, Champion champion) {
    this.game = game;
    this.isFirstPick = isFirstPick;
    this.selectOrder = selectOrder;
    this.type = type;
    this.champion = champion;
  }
}