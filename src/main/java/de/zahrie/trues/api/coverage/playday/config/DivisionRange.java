package de.zahrie.trues.api.coverage.playday.config;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;

public record DivisionRange(LeagueTier from, LeagueTier to) {

  public boolean isInside(LeagueTier tier) {
    return from.ordinal() <= tier.ordinal() && tier.ordinal() <= to.ordinal();
  }
}
