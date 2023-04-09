package de.zahrie.trues.api.coverage.playday.config;

import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.scheduler.SchedulingOption;
import de.zahrie.trues.api.coverage.stage.StageType;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTimeRange;
import lombok.Builder;

public record PlaydayConfig(StageType stageType, MatchFormat format, WeekdayTimeRange dayRange, List<SchedulingOption> options,
                            List<WeekdayTimeRange> customDays, TimeRepeater repeater) {
  @Builder
  @SuppressWarnings("unused")
  public PlaydayConfig {  }

  public WeekdayTimeRange playdayRange(int index) {
    return customDays() == null ? dayRange() : customDays().get(index - 1);
  }

}
