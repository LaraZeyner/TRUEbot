package de.zahrie.trues.api.coverage.match.log;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.player.PrimePlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Entity
@DiscriminatorValue("lineup_submit")
public class LineupMatchLog extends MatchLog implements Serializable {
  @Serial
  private static final long serialVersionUID = -1511303287998292492L;

  public List<Player> determineLineup() {
    return Arrays.stream(getDetails().split(", "))
        .map(playerString -> Chain.of(playerString).between(null, ":").intValue())
        .mapToInt(Integer::intValue)
        .mapToObj(PrimePlayerFactory::getPlayer)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
