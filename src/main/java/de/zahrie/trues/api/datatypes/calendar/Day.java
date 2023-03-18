package de.zahrie.trues.api.datatypes.calendar;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@RequiredArgsConstructor
public class Day implements Comparable<Day>, Serializable {
  @Serial
  private static final long serialVersionUID = 7454130509469830207L;
  private final int day;
  private final int month;
  private final int year;

  public Day(Time time) {
    this(time.get(Time.DAY_OF_MONTH), time.get(Time.MONTH), time.get(Time.YEAR));
  }

  public Time ofYear() {
    return new Time().replace(Time.DAY_OF_MONTH, day).replace(Time.MONTH, month).beginningOfDay();
  }

  public boolean hasPassed() {
    return new Time().getDay().compareTo(this) > 0;
  }

  public boolean isToday() {
    // TODO (Abgie) 15.03.2023: never used
    return equals(new Time().getDay());
  }

  public TimeRange next() {
    final Time next = nextTime();
    return new TimeRange(next, next.plus(Time.DATE, 1));
  }

  private Time nextTime() {
    final Time next = ofYear();
    return hasPassed() ? next.plus(Time.YEAR, 1) : next;
  }

  @Override
  public int compareTo(@NotNull Day o) {
    return Comparator.comparing(Day::getMonth).thenComparing(Day::getDay).compare(this, o);
  }
}
