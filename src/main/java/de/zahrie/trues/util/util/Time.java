package de.zahrie.trues.util.util;

import java.io.Serial;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    add(Calendar.DATE, dayOffset);
  }

}
