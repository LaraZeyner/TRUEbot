package de.zahrie.trues.api.datatypes.calendar;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.zahrie.trues.api.datatypes.symbol.Chain;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Time extends GregorianCalendar {
  @Serial
  private static final long serialVersionUID = -5477941385614028550L;

  public static Time of() {
    return new Time();
  }

  public static Time of(@Nullable Date date) {
    return date == null ? null : new Time(date);
  }

  public static Time fromEpoch(int seconds) {
    return new Time(new Date(seconds * 1000L));
  }

  public static Time min() {
    return fromEpoch(0);
  }

  public Time(Calendar timestamp) {
    this(timestamp.getTime());
  }

  public Time(@NonNull Date date) {
    this();
    setTime(date);
  }

  public Time() {
    super();
  }

  public Time(int dayOffset) {
    super();
    add(DATE, dayOffset);
  }

  public Time clock(Clock clock) {
    return clock(clock.hour(), clock.minute());
  }

  public Time clock(int hour, int minute) {
    return replace(HOUR, hour).replace(MINUTE, minute).replace(SECOND, 0).replace(MILLISECOND, 0);
  }

  public Time plus(int field, int amount) {
    final Time time1 = new Time(this);
    time1.add(field, amount);
    return time1;
  }

  public Time next(int dayOfWeek) {
    dayOfWeek = (dayOfWeek + 6) % 7;
    dayOfWeek = dayOfWeek == 0 ? 7 : dayOfWeek;
    final LocalDate date = LocalDate.of(get(YEAR), get(MONTH), get(DAY_OF_MONTH))
        .with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dayOfWeek)));
    final ZonedDateTime timeZoned = date.atStartOfDay(ZoneId.systemDefault());
    final Date from = Date.from(timeZoned.toInstant());
    return Time.of(from);
  }

  public Time replace(int field, int value) {
    final Time time1 = new Time();
    time1.set(field, value);
    return time1;
  }

  public Time beginningOfDay() {
    return new Time().replace(HOUR_OF_DAY, 0).replace(MINUTE, 0).replace(SECOND, 0).replace(MILLISECOND, 0);
  }

  public Day getDay() {
    return new Day(this);
  }

  public Chain chain(TimeFormat timeFormat) {
    return timeFormat.of(this);
  }
}
