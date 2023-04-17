package de.zahrie.trues.api.datatypes.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import de.zahrie.trues.util.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Getter
@RequiredArgsConstructor
@ToString
@Log
@ExtensionMethod(StringUtils.class)
public enum TimeFormat {
  DAY("dd.MM.", ""),
  HOUR_SHORT("HH", ""),
  HOUR("HH:mm", ""),
  DAY_LONG("dd.MM.YYYY", ""),
  DAY_STANDARD("YYYY-MM-dd", ""),
  DISCORD("<t:1234567890:R>", ""),
  DEFAULT("E, d. MMM YYYY HH:mm", ""),
  WEEKLY("EE., HH", " Uhr");

  private final String format;
  private final String suffix;

  public String of(LocalDateTime time) {
    if (equals(DISCORD)) return "<t:" + time.atZone(ZoneId.systemDefault()).toEpochSecond() + ":R>";
    return time.format(DateTimeFormatter.ofPattern(format)) + suffix;
  }

  public String of(LocalDate date) {
    return of(LocalDateTime.of(date, LocalTime.MIN));
  }

  public LocalDateTime of(String text) {
    if (equals(DISCORD)) return DateTimeUtils.fromEpoch(text.between("<t:", ":R>").intValue());
    return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(format));
  }

  public String now() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
  }
}
