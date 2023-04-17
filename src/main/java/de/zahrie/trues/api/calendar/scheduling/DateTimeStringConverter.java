package de.zahrie.trues.api.calendar.scheduling;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

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
@Data
@ExtensionMethod(StringUtils.class)
public final class DateTimeStringConverter {
  private final String input;

  public LocalDateTime toTime() {
    final TimeRange timeRange = toRangeList().stream().findFirst().orElse(null);
    return Util.avoidNull(timeRange, null, TimeRange::getStartTime);
  }

  public List<TimeRange> toRangeList() {
    final List<TimeRange> ranges = Arrays.stream(input.split("\n"))
        .flatMap(line -> determineRangesPerLine(line).stream()).toList();
    return TimeRange.combine(new ArrayList<>(ranges));
  }

  private List<TimeRange> determineRangesPerLine(String line) {
    line = line.replace(" Uhr", "h")
        .replace("&", " ")
        .replace(",", " ");
    final List<LocalDate> days = new ArrayList<>();
    final List<List<LocalTime>> times = new ArrayList<>();
    for (String section : line.split(" ")) {
      if (section.contains("@")) continue;
      if (Arrays.stream(section.split("-")).allMatch(sub -> sub.matches("\\d.") || sub.matches("\\d{2}.") || sub.matches("\\d{2}.\\d{2}.") || sub.matches("\\d{2}.\\d{2}.\\d{4}") || determineDayOfWeek(sub) != null)) {
        if (section.contains("-")) {
          final String[] splitted = section.split("-");
          if (splitted.length < 2) continue;
          final String startDay = splitted[0];
          final String endDay = splitted[1];
          days.addAll(handleDateRanges(startDay, endDay));
        } else {
          days.add(handleDateString(section));
        }

      } else if (Arrays.stream(section.split("-")).allMatch(subSection -> subSection.matches("\\d{2}h") || subSection.matches("\\d{2}:\\d{2}") || subSection.matches("\\d{2}:\\d{2}:\\d{2}") || subSection.matches("\\d{2}:\\d{2}h") || subSection.matches("\\d{2}:\\d{2}:\\d{2}h"))) {
        String startTime = "00:00:00";
        String endTime = "23:59:59";
        if (section.startsWith("-")) {
          endTime = section.split("-")[1];
        } else if (section.endsWith("-")) {
          startTime = section.replace("-", "");
        } else if (section.contains("-")) {
          startTime = section.split("-")[0];
          endTime = section.split("-")[1];
        } else {
          startTime = section;
        }
        times.add(List.of(getTime(startTime), getTime(endTime)));
      }
    }
    return days.stream().flatMap(day -> times.stream()
            .map(time -> new TimeRange(LocalDateTime.of(day, time.get(0)), LocalDateTime.of(day, time.get(1))))
            .toList().stream())
        .collect(Collectors.toList());
  }

  private DayOfWeek determineDayOfWeek(String day) {
    day = day.keep(2).capitalizeFirst();
    try {
      final TemporalAccessor temporalAccessor = DateTimeFormatter.ofPattern("E").withLocale(Locale.GERMANY).parse(day + ".");
      return DayOfWeek.from(temporalAccessor);
    } catch (DateTimeParseException ignored) {
      return null;
    }
  }

  private List<LocalDate> handleDateRanges(String startDay, String endDay) {
    final LocalDate startDate = handleDateString(startDay);
    final LocalDate endDate = handleDateString(endDay);
    if (startDate == null || endDate == null) return List.of();

    final long additionalDays = Duration.between(startDate, endDate).toDays();
    return IntStream.iterate(1, i -> i < additionalDays + 1, i -> i + 1)
        .mapToObj(i -> startDate.plus(i, ChronoUnit.DAYS)).toList();
  }

  /**
   * following pattern letters are defined:
   * <table class="striped">
   * <caption>Pattern Letters and Symbols</caption>
   * <thead>
   *  <tr><th scope="col">Symbol</th>   <th scope="col">Meaning</th>         <th scope="col">Presentation</th> <th scope="col">Examples</th>
   * </thead>
   * <tbody>
   *   <tr><th scope="row">y/u</th>     <td>year-of-era</td>                 <td>year</td>              <td>2004; 04</td>
   *   <tr><th scope="row">D</th>       <td>day-of-year</td>                 <td>number</td>            <td>189</td>
   *   <tr><th scope="row">M/L</th>     <td>month-of-year</td>               <td>number/text</td>       <td>7; 07; Jul; July; J</td>
   *   <tr><th scope="row">d</th>       <td>day-of-month</td>                <td>number</td>            <td>10</td>
   *
   *   <tr><th scope="row">Q/q</th>     <td>quarter-of-year</td>             <td>number/text</td>       <td>3; 03; Q3; 3rd quarter</td>
   *   <tr><th scope="row">w</th>       <td>week-of-week-based-year</td>     <td>number</td>            <td>27</td>
   *   <tr><th scope="row">W</th>       <td>week-of-month</td>               <td>number</td>            <td>4</td>
   *   <tr><th scope="row">E</th>       <td>day-of-week</td>                 <td>text</td>              <td>Tue; Tuesday; T</td>
   *   <tr><th scope="row">e/c</th>     <td>localized day-of-week</td>       <td>number/text</td>       <td>2; 02; Tue; Tuesday; T</td>
   *   <tr><th scope="row">F</th>       <td>aligned-week-of-month</td>       <td>number</td>            <td>3</td>
   *
   *   <tr><th scope="row">H</th>       <td>hour-of-day (0-23)</td>          <td>number</td>            <td>0</td>
   *   <tr><th scope="row">m</th>       <td>minute-of-hour</td>              <td>number</td>            <td>30</td>
   *   <tr><th scope="row">s</th>       <td>second-of-minute</td>            <td>number</td>            <td>55</td>
   *   <tr><th scope="row">S</th>       <td>fraction-of-second</td>          <td>fraction</td>          <td>978</td>
   *   <tr><th scope="row">A</th>       <td>milli-of-day</td>                <td>number</td>            <td>1234</td>
   *   <tr><th scope="row">n</th>       <td>nano-of-second</td>              <td>number</td>            <td>987654321</td>
   *   <tr><th scope="row">N</th>       <td>nano-of-day</td>                 <td>number</td>            <td>1234000000</td>
   *
   *   <tr><th scope="row">O</th>       <td>localized zone-offset</td>       <td>offset-O</td>          <td>GMT+8; GMT+08:00; UTC-08:00</td>
   *   <tr><th scope="row">x</th>       <td>zone-offset</td>                 <td>offset-x</td>          <td>+0000; -08; -0830; -08:30; -083015; -08:30:15</td>
   *   <tr><th scope="row">Z</th>       <td>zone-offset</td>                 <td>offset-Z</td>          <td>+0000; -0800; -08:00</td>
   *
   *   <tr><th scope="row">''</th>      <td>single quote</td>                <td>literal</td>           <td>'</td>
   *   <tr><th scope="row">[</th>       <td>optional section start</td>      <td></td>                  <td></td>
   *   <tr><th scope="row">]</th>       <td>optional section end</td>        <td></td>                  <td></td>
   * </tbody>
   * </table>
   */
  private LocalDate handleDateString(String section) {
    if (section.erase(1).contains(".")) {
      if (section.endsWith(".")) section = section.erase(-1);
      if (section.replace(".", "").intValue() == -1) return null;
      final String[] splitted = section.split("\\.");
      int day = section.intValue();
      int month = LocalDate.now().getMonthValue();
      int year = LocalDate.now().getYear();

      if (splitted.length > 0) {
        day = splitted[0].intValue();
        month = splitted[1].intValue();
        if (splitted.length > 2) year = splitted[2].intValue();
      }
      try {
        return LocalDate.of(year, month, day);
      } catch (DateTimeException ignored) {
        return null;
      }
    }
    final DayOfWeek dayOfWeek = determineDayOfWeek(section);
    if (dayOfWeek == null) return null;
    final int repeated = section.after("+").intValue(section.contains("+") ? 1 : 0);
    final LocalDate date = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayOfWeek));
    return date.plusWeeks(repeated);
  }

  private LocalTime getTime(String timeString) {
    final String origin = "00:00:00";
    timeString = timeString.replace("h", "");
    timeString = timeString.length() == 1 ? "0" + timeString + ":00:00" : timeString + origin.substring(timeString.length());
    final int hour = timeString.split(":")[0].intValue();
    final int minute = timeString.split(":")[1].intValue();
    final int second = timeString.split(":")[2].intValue();
    return LocalTime.of(hour, minute, second);
  }
}
