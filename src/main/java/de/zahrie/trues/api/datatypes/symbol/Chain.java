package de.zahrie.trues.api.datatypes.symbol;

import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import lombok.NonNull;
import org.slf4j.helpers.MessageFormatter;

public final class Chain extends ChainString {
  private Chain(String value) {
    super(value);
  }

  public static Chain of() {
    return new Chain("");
  }

  public static Chain of(String value) {
    return new Chain(value);
  }

  public static Chain of(char[] value) {
    return Chain.of(new String(value));
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public int intValue() {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ignored) {  }
    return -1;
  }

  public Time time(TimeFormat timeFormat) {
    return timeFormat.of(this);
  }

  public Chain add(Chain chain) {
    value += chain.toString();
    return this;
  }

  public Chain add(String toAppend) {
    value += toAppend;
    return this;
  }

  public Chain add(Number toAppend) {
    value += toAppend;
    return this;
  }

  public Chain add(char c) {
    value += "" + c;
    return this;
  }

  public Chain between(String start) {
    return between(start, null, 1);
  }

  public Chain between(String start, String end) {
    return between(start, end, 1);
  }

  public Chain between(String start, int occurrence) {
    return between(start, null, occurrence);
  }

  public Chain between(String start, String end, int occurrence) {
    int startIndex = -1;
    int endIndex = length();
    if (start != null) {
      startIndex = ordinalIndexOf(start, occurrence) + start.length() - 1;
      if (end != null) {
        endIndex = indexOf(end, startIndex + 1);
      }
    } else if (end != null) {
      endIndex = ordinalIndexOf(end, occurrence);
    }

    if (endIndex <= startIndex) {
      throw new IndexOutOfBoundsException("Index-Fehler");
    }
    return substring(startIndex + 1, endIndex);
  }

  public int ordinalIndexOf(String key, int ordinal) {
    int pos = lastIndexOf(key);
    if (ordinal < 0) {
      ordinal = Math.abs(ordinal);
      while (--ordinal > 0 && pos != -1)
        pos = lastIndexOf(key, pos - 1);
    } else {
      pos = indexOf(key);
      while (--ordinal > 0 && pos != -1)
        pos = indexOf(key, pos + 1);
    }
    return pos;
  }

  public Chain uncapitalizeFirst() {
    return substring(0, 1).lower().add(substring(1));
  }

  public Chain strip(int length) {
    return substring(0, Math.min(length, length()));
  }

  public Chain capitalizeFirst() {
    final char[] buffer = lower().toCharArray();
    boolean capitalizeNext = true;
    final char[] charArray = lower().toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      final char ch = charArray[i];
      if (Character.isSpaceChar(ch)) {
        capitalizeNext = true;
      } else if (capitalizeNext) {
        buffer[i] = Character.toTitleCase(ch);
        capitalizeNext = false;
      }
    }
    return Chain.of(buffer);
  }

  public Chain replace(@NonNull String sequence, int index) {
    return substring(0, index).add(Chain.of(sequence)).add(substring(index + sequence.length()));
  }

  public int count(String substring) {
    int count = 0, fromIndex = 0;
    while ((fromIndex = value.indexOf(substring, fromIndex)) != -1) {
      count++;
      fromIndex++;
    }
    return count;
  }

  public Chain format(Object... data) {
    return format(0, data);
  }

  public Chain format(int startingIndex, Object... data) {
    if (data.length == 0) {
      return this;
    }
    final List<Object> objects = Arrays.stream(data).toList().subList(startingIndex, count("{}"));
    final String message = MessageFormatter.arrayFormat(value, objects.toArray()).getMessage();
    return Chain.of(message);
  }

}
