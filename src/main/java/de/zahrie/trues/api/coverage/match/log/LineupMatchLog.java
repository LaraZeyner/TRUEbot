package de.zahrie.trues.api.coverage.match.log;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.util.util.Util;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;

/**
 * Created by Lara on 16.02.2023 for TRUEbot
 */
@AllArgsConstructor
@Entity
@DiscriminatorValue("lineup_submit")
public class LineupMatchLog extends MatchLog implements Serializable {
  @Serial
  private static final long serialVersionUID = -1511303287998292492L;

  public List<Player> determineLineup() {
    return Arrays.stream(getDetails().split(", "))
        .map(playerString -> Util.between(playerString, null, ":"))
        .mapToInt(Integer::parseInt)
        .mapToObj(PlayerFactory::getPlayer)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}