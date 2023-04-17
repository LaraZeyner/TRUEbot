package de.zahrie.trues.api.coverage.lineup;

import java.util.HashMap;
import java.util.Map;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;

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

  protected void only(Lane lane, Player player) {
    final LaneLineup laneLineup = laneLineups.get(lane);
    LaneGames games = null;
    if (laneLineup != null) {
      games = laneLineup.getPlayers().stream().filter(laneGames -> laneGames.player().equals(player)).findFirst().orElse(null);
      laneLineups.remove(lane);
    }
    add(lane, player, games != null ? games.amount() : 0);
  }

  protected void sort() {
    laneLineups.forEach((a, b) -> b.sort());
  }

}
