package de.zahrie.trues.api.coverage.playday.scheduler;

import de.zahrie.trues.api.coverage.playday.config.DivisionRange;
import de.zahrie.trues.api.coverage.playday.config.RelativeTimeRange;
import de.zahrie.trues.api.coverage.playday.config.TimeOffset;
import lombok.Builder;

public record SchedulingOption(DivisionRange divisionRange, TimeOffset defaultTime, RelativeTimeRange range) {

  @Builder
  public SchedulingOption {  }
}
