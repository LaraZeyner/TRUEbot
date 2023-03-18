package de.zahrie.trues.api.datatypes.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.zahrie.trues.api.datatypes.symbol.Chain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;

@Getter
@RequiredArgsConstructor
@ToString
@Log
public enum TimeFormat {
  DAY("dd.MM."),
  DEFAULT("E, d. MMM YYYY HH:mm");

  private final String format;

  private SimpleDateFormat format() {
    return new SimpleDateFormat(this.format);
  }

  public Chain now() {
    // TODO (Abgie) 15.03.2023: never used
    return this.of(new Time());
  }

  public Time of(Chain dateString) {
    try {
      return Time.of(format().parse(dateString.toString()));
    } catch (ParseException parseException) {
      log.throwing("TimeFormat", "of(Chain): Time", parseException);
    }
    return Time.min();
  }

  public Chain of(Time time) {
    return Chain.of(format().format(time.getTime()));
  }
}
