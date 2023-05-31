package de.zahrie.trues.api.calendar.scheduling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.util.Util;
import de.zahrie.trues.util.io.log.Console;
import de.zahrie.trues.util.io.log.DevInfo;
import org.jetbrains.annotations.Nullable;

/**
 * <b>Allowed Dates</b> <br>
 * 12.23.4567 <br>
 * 12.23. <br>
 * 12. <br>
 * Mo <br>
 * - <br>
 * <b>Allowed Times</b> <br>
 * 12:34:56 <br>
 * 12:34 <br>
 * 12h <br>
 */
public record DateTimeStringConverter(String input) {
  private static final List<String> DAY_PATTERNS = List.of("\\d\\.", "\\d{2}\\.", "\\d{2}\\.\\d{2}\\.", "\\d{2}\\.\\d{2}\\.\\d{4}");
  private static final List<String> TIME_PATTERNS = List.of("\\dh", "\\d{2}h", "\\d{2}:\\d{2}", "\\d{2}:\\d{2}:\\d{2}", "\\d{2}:\\d{2}h", "\\d{2}:\\d{2}:\\d{2}h");
  @Nullable
  public LocalDateTime toTime() {
    final TimeRange timeRange = toRangeList().stream().findFirst().orElse(null);
    return Util.avoidNull(timeRange, null, TimeRange::getStartTime);
  }

  public List<TimeRange> toRangeList() {
    final List<TimeRange> ranges = Arrays.stream(input.split("\n"))
        .flatMap(line -> determineRangesPerLine(line).stream()).collect(Collectors.toList());
    return TimeRange.combine(ranges);
  }

  private List<TimeRange> determineRangesPerLine(String line) {
    int trashLines = 0;
    line = line.replace(" Uhr", "h")
        .replace("&", " ")
        .replace(",", " ");
    final List<LocalDate> days = new ArrayList<>();
    final List<List<LocalTime>> times = new ArrayList<>();
    for (String section : line.strip().split(" ")) {
      if (section.contains("@")) continue;

      final List<String> subSections = Arrays.stream(section.split("-")).map(String::strip).filter(s -> !s.isBlank()).toList();
      if (subSections.isEmpty()) return List.of();

      if (subSections.stream().allMatch(subSection -> DAY_PATTERNS.stream().anyMatch(subSection::matches) ||
          DateStringConverter.determineDayOfWeek(subSection) != null)) {
        final List<LocalDate> dates = new DateStringConverter(section).toList();
        days.addAll(dates);
      } else if (subSections.stream().allMatch(subSection -> TIME_PATTERNS.stream().anyMatch(subSection::matches))) {
        times.add(new TimeStringConverter(section).toList());
      }

      for (String subSection : subSections) {
        subSection = subSection.strip();
        if (subSection.isBlank()) continue;

        if (DAY_PATTERNS.stream().noneMatch(subSection::matches) && DateStringConverter.determineDayOfWeek(subSection) == null &&
        TIME_PATTERNS.stream().noneMatch(subSection::matches)) {
          trashLines += (subSection.length() + 1);
        }
       }
    }
    if (trashLines > line.length() / 2) return List.of();

    if (days.isEmpty()) days.add(LocalDate.now());
    if (times.isEmpty()) times.add(List.of(LocalTime.MIN, LocalTime.MAX));
    try {
      return days.stream().flatMap(day -> times.stream()
              .map(time -> new TimeRange(LocalDateTime.of(day, time.get(0)), LocalDateTime.of(day, time.get(1))))
              .toList().stream())
          .collect(Collectors.toList());
    } catch (NullPointerException exception) {
      new DevInfo("__" + line + "__ konnte nicht formatiert werden").with(Console.class).severe(exception);
      throw new RuntimeException(exception);
    }
  }
}
