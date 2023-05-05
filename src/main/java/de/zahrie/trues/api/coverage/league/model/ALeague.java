package de.zahrie.trues.api.coverage.league.model;

import de.zahrie.trues.api.coverage.stage.model.Stage;
import org.jetbrains.annotations.NotNull;

public interface ALeague extends Comparable<ALeague> {
  Stage getStage();
  String getName();

  default LeagueTier getTier() {
    return LeagueTier.fromName(getName());
  }

  @Override
  default int compareTo(@NotNull ALeague o) {
    return getStage().compareTo(o.getStage());
  }
}
