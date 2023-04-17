package de.zahrie.trues.api.coverage.lineup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class LaneLineup implements Comparable<LaneLineup> {
  private final Lane lane;
  private final List<LaneGames> players;

  public LaneLineup(Lane lane) {
    this.lane = lane;
    this.players = new ArrayList<>();
  }

  public void sort() {
    players.sort(Comparator.comparing(LaneGames::amount).reversed());
  }

  @Override
  public int compareTo(@NotNull LaneLineup o) {
    return Comparator.comparing(LaneLineup::getLane).compare(this, o);
  }
}
