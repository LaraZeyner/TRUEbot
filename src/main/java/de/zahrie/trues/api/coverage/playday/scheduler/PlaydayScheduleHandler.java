package de.zahrie.trues.api.coverage.playday.scheduler;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.playday.RepeatType;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.playday.config.PlaydayRange;
import de.zahrie.trues.api.coverage.playday.config.RelativeTimeRange;
import de.zahrie.trues.api.coverage.playday.config.SchedulingRange;
import de.zahrie.trues.api.datatypes.calendar.TimeOffset;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.datatypes.calendar.Time;

public record PlaydayScheduleHandler(PlayStage stage, int index, LeagueTier tier) {

  public PlaydayScheduler create() {
    final PlaydayConfig config = stage.playdayConfig();
    final TimeRange playdayTimeRange = fromAbstractRange(config.determineRange(index));
    final var playdayRange = new PlaydayRange(playdayTimeRange.start(), playdayTimeRange.end());

    final var scheduling = new Scheduling(config.options());
    final TimeRange schedulingTimeRange = fromAbstractRange(scheduling.range(tier));
    final var schedulingRange = new SchedulingRange(schedulingTimeRange.start(), schedulingTimeRange.end());

    final TimeOffset timeOffset = scheduling.defaultTime(tier);
    final Time defaultTime = fromOffset(timeOffset, playdayTimeRange.start());
    return new PlaydayScheduler(playdayRange, defaultTime, schedulingRange);
  }

  public TimeRange fromAbstractRange(RelativeTimeRange range) {
    final PlaydayConfig config = stage.playdayConfig();

    final Time start = fromOffset(range.start(), (Time) stage.getStart());
    if (config.customDays() == null) {
      final RepeatType repeatType = config.repeater().type();
      start.add(Time.DATE, repeatType.getDays() * (index - 1));
    }

    final Time end = fromOffset(range.end(), start);
    return new TimeRange(start, end);
  }

  private Time fromOffset(TimeOffset offset, Time start) {
    final Time offsetTime = offset.day() > 0 && offset.day() < 8 ? start.next(offset.day()) : start.plus(Time.DATE, offset.day());
    return offsetTime.clock(offset.clock());
  }

}
