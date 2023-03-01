package de.zahrie.trues.api.coverage.playday.config;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;

/**
 * Created by Lara on 27.02.2023 for TRUEbot
 */
public record DivisionRange(LeagueTier from, LeagueTier to) {

  public boolean isInside(LeagueTier tier) {
    return from.ordinal() <= tier.ordinal() &&
        tier.ordinal() <= to.ordinal();
  }
}
