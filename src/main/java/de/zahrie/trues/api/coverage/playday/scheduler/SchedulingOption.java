package de.zahrie.trues.api.coverage.playday.scheduler;

import de.zahrie.trues.api.coverage.playday.config.DivisionRange;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTime;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTimeRange;
import lombok.Builder;

public record SchedulingOption(DivisionRange divisionRange, WeekdayTime defaultTime, WeekdayTimeRange range) {

  @Builder
  @SuppressWarnings("unused")
  public SchedulingOption {  }
}
