package de.zahrie.trues.api.coverage.playday.config;

import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.scheduler.SchedulingOption;
import de.zahrie.trues.api.coverage.stage.StageType;
import lombok.Builder;

public record PlaydayConfig(StageType stageType, MatchFormat format, RelativeTimeRange dayRange, List<SchedulingOption> options,
                            List<RelativeTimeRange> customDays, TimeRepeater repeater) {
  @Builder
  @SuppressWarnings("unused")
  public PlaydayConfig {  }

  public RelativeTimeRange determineRange(int index) {
    return customDays() == null ? dayRange() : customDays().get(index - 1);
  }

}
