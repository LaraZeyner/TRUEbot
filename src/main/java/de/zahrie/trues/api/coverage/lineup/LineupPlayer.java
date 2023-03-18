package de.zahrie.trues.api.coverage.lineup;

import java.util.Map;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;


public record LineupPlayer(Player player, Map<Lane, Integer> amountOfGames) {
  // TODO (Abgie) 27.02.2023:

  public void add(Lane lane, Integer integer) {
    amountOfGames.put(lane, integer);
  }

}
