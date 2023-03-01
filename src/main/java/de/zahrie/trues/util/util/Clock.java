package de.zahrie.trues.util.util;

/**
 * Created by Lara on 27.02.2023 for TRUEbot
 */
public record Clock(int hour, int minute) {
  public Clock() {
    this(0, 0);
  }

  public Clock add(int minutes) {
    int totalMinutes = minutes + hour * 60 + minute;
    totalMinutes %= 1440;
    return new Clock(totalMinutes / 60, totalMinutes % 60);
  }

}
