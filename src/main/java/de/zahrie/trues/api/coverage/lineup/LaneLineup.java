package de.zahrie.trues.api.coverage.lineup;

import java.util.Comparator;

import de.zahrie.trues.api.riot.Lane;
import de.zahrie.trues.api.datatypes.collection.Stack;
import lombok.Getter;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
@Getter
public class LaneLineup {
  private final Lane lane;
  private final Stack<LaneGames> players;

  public LaneLineup(Lane lane) {
    this.lane = lane;
    this.players = new Stack<>();
  }

  public void sort() {
    players.sort(Comparator.comparing(LaneGames::amount).reversed());
  }

}
