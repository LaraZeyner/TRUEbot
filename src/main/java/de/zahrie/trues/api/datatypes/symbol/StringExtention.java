package de.zahrie.trues.api.datatypes.symbol;

import de.zahrie.trues.api.datatypes.calendar.Time;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class StringExtention {
  public static String upper(String value) {
    return value.toUpperCase();
  }

  public static <T extends Enum<T>> T toEnum(String value, Class<T> clazz) {
    final String replace = upper(value).replace(" ", "_");
    return Enum.valueOf(clazz, replace);
  }

  /**
   * @param start Wenn <code>start = null oder nicht in value</code>, dann startindex immer 0
   * @return Sequenz zwischen entsprechenden Werten
   */
  public static String between(@NonNull String value, @NonNull String start) {
    return between(value, start, null, 1);
  }

  /**
   * @param start Wenn <code>start = null oder nicht in value</code>, dann startindex immer 0
   * @param end Wenn <code>end = null oder nicht in value</code>, dann max length
   * @return Sequenz zwischen entsprechenden Werten
   */
  public static String between(@NonNull String value, @Nullable String start, @NonNull String end) {
    return between(value, start, end, 1);
  }

  /**
   * @param start Wenn <code>start = null oder nicht in value</code>, dann startindex immer 0
   * @return Sequenz zwischen entsprechenden Werten
   */
  public static String between(@NonNull String value, @NonNull String start, int occurrence) {
    return between(value, start, null, occurrence);
  }

  /**
   * @param start Wenn <code>start = null oder nicht in value</code>, dann startindex immer 0
   * @param end Wenn <code>end = null oder nicht in value</code>, dann max length
   * @return Sequenz zwischen entsprechenden Werten
   */
  public static String between(@NonNull String value, @Nullable String start, @Nullable String end, int occurrence) {
    int startIndex = -1;
    int endIndex = value.length();
    if (start != null) {
      startIndex = ordinalIndexOf(value, start, occurrence) + start.length() - 1;
      if (end != null) {
        endIndex = value.indexOf(end, startIndex + 1);
      }
    } else if (end != null) {
      endIndex = ordinalIndexOf(value, end, occurrence);
    }

    if (endIndex <= startIndex) {
      throw new IndexOutOfBoundsException("Index-Fehler");
    }
    if (endIndex == -1) {
      endIndex = value.length();
    }
    return value.substring(startIndex + 1, endIndex);
  }

  /**
   * @return Index, wo der String zum {@code n}-ten Mal auftritt. <br>
   * Wenn nicht vorhanden, dann {@code -1}
   */
  public static int ordinalIndexOf(String value, String key, int ordinal) {
    int pos = value.lastIndexOf(key);
    if (ordinal < 0) {
      ordinal = Math.abs(ordinal);
      while (--ordinal > 0 && pos != -1)
        pos = value.lastIndexOf(key, pos - 1);
    } else {
      pos = value.indexOf(key);
      while (--ordinal > 0 && pos != -1)
        pos = value.indexOf(key, pos + 1);
    }
    return pos;
  }

  public static Integer intValue(String value) {
    return intValue(value, -1);
  }

  public static Integer intValue(String value, Integer defaultValue) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ignored) {  }
    return defaultValue;
  }

  public static Time getTime(String value) {
    //TODO (Abgie) 21.03.2023:
    return null;
  }

  public static int countMatches(String value, String  sub) {
    if (value.isEmpty() || sub.isEmpty()) return 0;
    int count = 0;
    int idx = 0;
    while ((idx = value.indexOf(sub, idx)) != -1) {
      count++;
      idx += sub.length();
    }
    return count;
  }
}
