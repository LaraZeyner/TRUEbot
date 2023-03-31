package de.zahrie.trues.api.coverage.lineup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import lombok.Getter;

@Getter
public class LaneLineup {
  private final Lane lane;
  private final List<LaneGames> players;

  public LaneLineup(Lane lane) {
    this.lane = lane;
    this.players = new ArrayList<>();
  }

  public void sort() {
    players.sort(Comparator.comparing(LaneGames::amount).reversed());
  }

}
