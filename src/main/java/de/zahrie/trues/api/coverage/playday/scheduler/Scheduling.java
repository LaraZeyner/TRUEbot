package de.zahrie.trues.api.coverage.playday.scheduler;

import java.util.List;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.playday.config.RelativeTimeRange;
import de.zahrie.trues.api.datatypes.calendar.TimeOffset;

public class Scheduling {
  private final List<SchedulingOption> options;

  public Scheduling(List<SchedulingOption> options) {
    this.options = options;
  }

  public RelativeTimeRange range(LeagueTier tier) {
    return options.stream().filter(option -> option.divisionRange().isInside(tier))
        .map(SchedulingOption::range).findFirst().orElse(null);
  }

  public TimeOffset defaultTime(LeagueTier tier) {
    return options.stream().filter(option -> option.divisionRange().isInside(tier))
        .map(SchedulingOption::defaultTime).findFirst().orElse(null);
  }

}
