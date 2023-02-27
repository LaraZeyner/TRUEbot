package de.zahrie.trues.api.coverage.lineup;

import java.util.Map;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.models.riot.Lane;

/**
 * Created by Lara on 20.02.2023 for TRUEbot
 */
public record LineupPlayer(Player player, Map<Lane, Integer> amountOfGames) {

  public void add(Lane lane, Integer integer) {
    amountOfGames.put(lane, integer);
  }

}
