package de.zahrie.trues.api.coverage.lineup;

import java.util.HashMap;
import java.util.Map;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
public class LineupCreatorBase {
  protected final Map<Lane, LaneLineup> laneLineups = new HashMap<>();

  protected void add(Lane lane, Player player, int amount) {
    final var laneGames = new LaneGames(player, amount);
    if (!laneLineups.containsKey(lane)) {
      laneLineups.put(lane, new LaneLineup(lane));
    }
    final LaneLineup laneLineup = laneLineups.get(lane);
    laneLineup.getPlayers().add(laneGames);
  }

  protected void sort() {
    laneLineups.forEach((a, b) -> b.sort());
  }

}
