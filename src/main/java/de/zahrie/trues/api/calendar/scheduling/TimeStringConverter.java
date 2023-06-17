package de.zahrie.trues.api.calendar.scheduling;

import java.time.LocalTime;
import java.util.List;

import de.zahrie.trues.util.StringUtils;

public record TimeStringConverter(String input) {
  List<LocalTime> toList() {
    String startTime = "00:00:00";
    String endTime = "23:59:59";
    if (input.startsWith("-")) {
      endTime = input.split("-")[1];
    } else if (input.endsWith("-")) {
      startTime = input.replace("-", "");
    } else if (input.contains("-")) {
      startTime = input.split("-")[0];
      endTime = input.split("-")[1];
    } else {
      startTime = input;
    }
    return List.of(new TimeStringConverter(startTime).getTime(), new TimeStringConverter(endTime).getTime());
  }

  public LocalTime getTime() {
    final String origin = "00:00:00";
    String timeString = input.replace("h", "");
    timeString = timeString.length() == 1 ? "0" + timeString + ":00:00" : timeString + origin.substring(timeString.length());
    final int hour = StringUtils.intValue(timeString.split(":")[0]);
    final int minute = StringUtils.intValue(timeString.split(":")[1]);
    final int second = StringUtils.intValue(timeString.split(":")[2]);
    return LocalTime.of(hour, minute, second);
  }
}
