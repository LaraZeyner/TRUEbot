package de.zahrie.trues.api.coverage.lineup;

import java.util.Comparator;

import de.zahrie.trues.models.riot.Lane;
import de.zahrie.trues.util.util.TrueList;
import lombok.Getter;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
@Getter
public class LaneLineup {
  private final Lane lane;
  private final TrueList<LaneGames> players;

  public LaneLineup(Lane lane) {
    this.lane = lane;
    this.players = new TrueList<>();
  }

  public void sort() {
    players.sort(Comparator.comparing(LaneGames::amount).reversed());
  }

}
