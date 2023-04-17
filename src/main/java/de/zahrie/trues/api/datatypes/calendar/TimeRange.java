package de.zahrie.trues.api.datatypes.calendar;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.calendar.TeamCalendar;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
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

  public String display() {
    final LocalDateTime now = LocalDateTime.now();
    final TimeFormat format = startTime.isAfter(now.plusWeeks(3)) || startTime.isBefore(now.minusWeeks(3)) ? TimeFormat.DEFAULT : TimeFormat.DISCORD;
    return format.of(startTime);
  }

  public String displayRange() {
    if (startTime.isAfter(LocalDateTime.now())) return "Ende " + TimeFormat.DISCORD.of(endTime);
    return "Beginn " + TimeFormat.DISCORD.of(startTime);
  }

  public String duration() {
    final long minutes = Duration.between(startTime, endTime).get(ChronoUnit.MINUTES);
    final int realMinutes = (int) (minutes % 60);
    return minutes / 60 + ":" + (realMinutes < 10 ? "0" : "") + realMinutes + " Stunden";
  }

  public String trainingReserved(OrgaTeam orgaTeam) {
    return isReserved(orgaTeam) ? "ja" : "nein";
  }

  private boolean isReserved(OrgaTeam orgaTeam) {
    for (final TeamCalendar calendarEntry : orgaTeam.getScheduler().getCalendarEntries()) {
      final TimeRange range = calendarEntry.getRange();
      if (range.getStartTime().isAfterEqual(endTime)) continue;
      if (range.getEndTime().isBeforeEqual(startTime)) continue;
      return true;
    }
    return false;
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

  public static List<TimeRange> intersect(List<TimeRange> ranges1, List<TimeRange> ranges2) {
    return combine(ranges1.stream().flatMap(range1 -> range1.intersect(ranges2).stream()).collect(Collectors.toList()));
  }

  private List<TimeRange> intersect(List<TimeRange> ranges) {
    final List<TimeRange> newRanges = new ArrayList<>();
    for (final TimeRange range : ranges) {
      if (range.getStartTime().isBeforeEqual(endTime) && startTime.isBeforeEqual(range.getEndTime())) {
        final LocalDateTime start = range.getStartTime().isAfter(startTime) ? range.getStartTime() : startTime;
        final LocalDateTime end = range.getEndTime().isBefore(endTime) ? range.getEndTime() : endTime;
        newRanges.add(new TimeRange(start, end));
      }
    }
    return newRanges;
  }

  @Override
  public int compareTo(@NotNull TimeRange o) {
    return Comparator.comparing(TimeRange::getStartTime).thenComparing(TimeRange::getEndTime).compare(this, o);
  }

  @Override
  public String toString() {
    TimeFormat format = (startTime.getMinute() + endTime.getMinute() == 0) ? TimeFormat.HOUR_SHORT : TimeFormat.HOUR;
    if (endTime.toLocalTime().equals(LocalTime.MAX)) {
      if (startTime.toLocalTime().equals(LocalTime.MIN)) return "ganzer Tag";
      format = (startTime.getMinute() == 0) ? TimeFormat.HOUR_SHORT : TimeFormat.HOUR;
      return "ab " + format.of(startTime) + " Uhr";
    }
    if (startTime.toLocalTime().equals(LocalTime.MIN)) return "bis " + format.of(endTime) + " Uhr";
    return format.of(startTime) + "-" + format.of(endTime);
  }
}
