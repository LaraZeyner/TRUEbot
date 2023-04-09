package de.zahrie.trues.api.datatypes.calendar;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {
  public static int daysBetween(DayOfWeek startDay, DayOfWeek endDay) {
    final int daysBetween = endDay.getValue() - startDay.getValue();
    return daysBetween < 0 ? daysBetween + 7 : daysBetween;
  }

  public static boolean isAfterEqual(LocalDateTime localDateTime, LocalDateTime other) {
    return localDateTime.isAfter(other) || localDateTime.isEqual(other);
  }

  public static boolean isBeforeEqual(LocalDateTime localDateTime, LocalDateTime other) {
    return localDateTime.isBefore(other) || localDateTime.isEqual(other);
  }

  public static LocalDateTime min(LocalDateTime localDateTime, LocalDateTime other) {
    return localDateTime.isBefore(other) ? localDateTime : other;
  }

  public static LocalDateTime max(LocalDateTime localDateTime, LocalDateTime other) {
    return localDateTime.isAfter(other) ? localDateTime : other;
  }

  public static LocalDateTime fromEpoch(int epochSeconds) {
    final Instant instant = Instant.ofEpochSecond(epochSeconds);
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }
}
