package de.zahrie.trues.api.datatypes.calendar;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@ExtensionMethod(DateTimeUtils.class)
public class TimeRange implements Comparable<TimeRange> {
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  public TimeRange(LocalDateTime startTime, int minutes, TemporalUnit unit) {
    this.startTime = startTime;
    this.endTime = startTime.plus(minutes, unit);
  }

  public TimeRange plusWeeks(int weeks) {
    return new TimeRange(startTime.plusWeeks(weeks), endTime.plusWeeks(weeks));
  }

  public boolean hasStarted() {
    return startTime.isAfter(LocalDateTime.now());
  }

  public boolean hasRunning() {
    return hasStarted() && !hasEnded();
  }

  public boolean hasEnded() {
    return endTime.isAfter(LocalDateTime.now());
  }

  public static List<TimeRange> reduce(List<TimeRange> from, List<TimeRange> minus) {
    from.sort(TimeRange::compareTo);
    minus.sort(TimeRange::compareTo);
    final ArrayList<TimeRange> newTimeRanges = new ArrayList<>(from);
    for (TimeRange minusRange : minus) {
      for (final TimeRange newRange : newTimeRanges) {
        if (minusRange.getStartTime().isBeforeEqual(newRange.getStartTime())) {
          if (minusRange.getEndTime().isBeforeEqual(newRange.getStartTime())) continue;
          newTimeRanges.remove(newRange);
          if (minusRange.getEndTime().isBefore(newRange.getEndTime())) {
            newTimeRanges.add(new TimeRange(minusRange.getEndTime(), newRange.getEndTime()));
          }
          continue;
        }

        if (minusRange.getEndTime().isAfterEqual(newRange.getEndTime())) {
          if (minusRange.getStartTime().isAfterEqual(newRange.getEndTime())) continue;
          newTimeRanges.remove(newRange);
          if (minusRange.getStartTime().isAfter(newRange.getStartTime())) {
            newTimeRanges.add(new TimeRange(newRange.getStartTime(), minusRange.getStartTime()));
          }
          continue;
        }

        newTimeRanges.remove(newRange);
        newTimeRanges.add(new TimeRange(newRange.getStartTime(), minusRange.getStartTime()));
        newTimeRanges.add(new TimeRange(minusRange.getEndTime(), newRange.getEndTime()));
      }
    }
    return newTimeRanges;
  }

  public static List<TimeRange> combine(List<TimeRange> timeRanges) {
    timeRanges.sort(TimeRange::compareTo);
    if (timeRanges.size() < 2) return timeRanges;
    TimeRange rangeOld = timeRanges.get(0);
    for (TimeRange range : timeRanges.subList(1, timeRanges.size())) {
      if (rangeOld.getEndTime().isAfterEqual(range.getStartTime())) {
        rangeOld = new TimeRange(rangeOld.getStartTime().min(range.getStartTime()), rangeOld.getEndTime().max(range.getEndTime()));
      } else {
        timeRanges.add(rangeOld);
        rangeOld = range;
      }
    }
    timeRanges.add(rangeOld);
    return timeRanges;
  }

  @Override
  public int compareTo(@NotNull TimeRange o) {
    return Comparator.comparing(TimeRange::getStartTime).thenComparing(TimeRange::getEndTime).compare(this, o);
  }
}
