package de.zahrie.trues.util.util;

import java.io.Serial;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 20.02.2023 for TRUEbot
 */
@Getter
public class Time extends GregorianCalendar {
  @Serial
  private static final long serialVersionUID = -5477941385614028550L;

  @Getter
  @RequiredArgsConstructor
  @ToString
  public enum TimeFormat {
    DEFAULT("E, d. MMM YYYY HH:mm");

    private final String format;


    public Date of(String date) {
      try {
        return new SimpleDateFormat(this.format).parse(date);
      } catch (ParseException ignored) { }
      return null;
    }

    public String now() {
      return this.of(new Date());
    }

    public String of(Date date) {
      return new SimpleDateFormat(this.format).format(date);
    }

  }


  public Time(Calendar timestamp) {
    this(timestamp.getTime());
  }

  public Time(Date date) {
    super();
    setTime(date);
  }

  public static Time fromEpoch(int seconds) {
    return new Time(new Date(seconds * 1000L));
  }

  public static Time min() {
    return fromEpoch(0);
  }

  public Time() {
    super();
  }

  public Time(int dayOffset) {
    super();
    add(DATE, dayOffset);
  }

  public Time clock(Clock clock) {
    clock(clock.hour(), clock.minute());
    return this;
  }

  public Time clock(int hour, int minute) {
    set(HOUR, hour);
    set(MINUTE, minute);
    set(SECOND, 0);
    set(MILLISECOND, 0);
    return this;
  }

  public Time plus(int field, int amount) {
    final Time time1 = new Time(this);
    time1.add(field, amount);
    return time1;
  }

  public Time next(int dayOfWeek) {
    if (get(DAY_OF_WEEK) != dayOfWeek) {
      dayOfWeek = (dayOfWeek + 6) % 7;
      dayOfWeek = dayOfWeek == 0 ? 7 : dayOfWeek;

      final LocalDate date = LocalDate.of(get(YEAR), get(MONTH), get(DAY_OF_MONTH))
          .with(TemporalAdjusters.next(DayOfWeek.of(dayOfWeek)));
      final ZonedDateTime timeZoned = date.atStartOfDay(ZoneId.systemDefault());
      final Date from = Date.from(timeZoned.toInstant());
      return new Time(from);
    }
    return new Time(getTime());
  }

}
