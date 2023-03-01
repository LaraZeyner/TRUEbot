package de.zahrie.trues.util.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.NonNull;

public final class Util {

  public static String between(String text, String start, String end) {
    return Util.between(text, start, end, 1);
  }

  public static String between(String text, String start, String end, int occurrence) {
    int startIndex = -1;
    int endIndex = text.length();
    if (start != null) {
      startIndex = Util.ordinalIndexOf(text, start, occurrence) + start.length() - 1;
      if (end != null) {
        endIndex = text.indexOf(end, startIndex + 1);
      }
    } else if (end != null) {
      endIndex = Util.ordinalIndexOf(text, end, occurrence);
    }

    if (endIndex <= startIndex) {
      throw new IndexOutOfBoundsException("Index-Fehler");
    }
    return text.substring(startIndex + 1, endIndex);
  }

  public static int ordinalIndexOf(String text, String key, int ordinal) {
    int pos = text.lastIndexOf(key);
    if (ordinal < 0) {
      ordinal = Math.abs(ordinal);
      while (--ordinal > 0 && pos != -1)
        pos = text.lastIndexOf(key, pos - 1);
    } else {
      pos = text.indexOf(key);
      while (--ordinal > 0 && pos != -1)
        pos = text.indexOf(key, pos + 1);
    }
    return pos;
  }

  public static String uncapitalizeFirst(String str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1);
  }

  public static String strip(String text, int length) {
    return text.substring(0, Math.min(length, text.length()));
  }

  public static String capitalize(String str) {
    str = str.toLowerCase();
    final char[] buffer = str.toCharArray();
    boolean capitalizeNext = true;
    char[] charArray = str.toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      final char ch = charArray[i];
      if (Character.isSpaceChar(ch)) {
        capitalizeNext = true;
      } else if (capitalizeNext) {
        buffer[i] = Character.toTitleCase(ch);
        capitalizeNext = false;
      }
    }
    return new String(buffer);
  }

  public static String replace(@NonNull String text, @NonNull String sequence, int index) {
    return text.substring(0, index) + sequence + text.substring(index + sequence.length());
  }

  public static int getInt(Object object) {
    return object != null ? (int) (((Long) object).longValue()) : 0;
  }

  public static int longToInt(Long l) {
    return Integer.parseInt(String.valueOf(l));
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

}
