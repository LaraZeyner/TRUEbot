package de.zahrie.trues.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

public final class Util {
  public static int getInt(Object object) {
    return object != null ? (int) (((Long) object).longValue()) : 0;
  }

  public static int longToInt(Long l) {
    return Integer.parseInt(String.valueOf(l));
  }

  public static String formatDuration(int seconds) {
    if (seconds < 60) return ":" + seconds;
    if (seconds < 60*60) return seconds/60 + ":" + seconds%60;
    return seconds/(60*60) + ":" + seconds%(60*60) + ":" + seconds%60;
  }

  public static String until(Date start, String prefix) {
    final long distance = Math.abs((System.currentTimeMillis() - start.getTime()) / 1000);

    final int seconds = (int) (distance % 60);
    final String secondsString = ("00" + seconds).substring(("00" + seconds).length() - 2);
    final int minutes = (int) ((distance / 60) % 60);
    final String minutesString = ("00" + minutes).substring(("00" + minutes).length() - 2);
    final int hours = (int) ((distance / 3_600) % 24);
    final String hoursString = ("00" + hours).substring(("00" + hours).length() - 2);
    final int days = (int) (distance / 86_400);

    final StringBuilder str = new StringBuilder(prefix);
    if (days > 2) {
      return new SimpleDateFormat("dd.MM. HH:mm").format(start);
    } else if (days > 1) {
      str.append(days).append("d ").append(hoursString).append(":").append(minutesString).append(":").append(secondsString);
    } else if (hours > 1) {
      str.append(days * 24 + hours).append(":").append(minutesString).append(":").append(secondsString);
    } else if (minutes > 1) {
      str.append(hours * 60 + minutes).append(":").append(secondsString);
    } else {
      str.append(minutes * 60 + seconds).append("s");
    }
    return str.toString();
  }

  public static Date getDate(LocalDateTime dateToConvert) {
    return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDateTime getLocalDate(Date dateToConvert) {
    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static <T> T nonNull(T obj) {
    return Util.nonNull(obj);
  }

  public static <T, R> R avoidNull(T obj, R other, Function<T, R> consumer) {
    if (obj == null) {
      return other;
    }
    return consumer.apply(obj);
  }

  public static <T> T nonNull(T obj, String message) {
    return Util.nonNull(obj, message);
  }

  public static <T> T avoidNull(T obj, T other) {
    return obj == null ? other : obj;
  }
}
