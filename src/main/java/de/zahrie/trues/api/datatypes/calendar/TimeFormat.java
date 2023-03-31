package de.zahrie.trues.api.datatypes.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Getter
@RequiredArgsConstructor
@ToString
@Log
@ExtensionMethod(StringExtention.class)
public enum TimeFormat {
  DAY("dd.MM."),
  DISCORD("<t:1234567890:R>"),
  DEFAULT("E, d. MMM YYYY HH:mm");

  private final String format;

  private SimpleDateFormat format() {
    return new SimpleDateFormat(this.format);
  }

  public String now() {
    // TODO (Abgie) 15.03.2023: never used
    return of(new Time());
  }

  public Time of(String dateString) {
    if (equals(DISCORD)) {
      final int epoch = dateString.between("<t:", ":R>").intValue();
      return Time.fromEpoch(epoch);
    }

    try {
      return Time.of(format().parse(dateString));
    } catch (ParseException parseException) {
      log.throwing("TimeFormat", "of(Chain): Time", parseException);
    }
    return Time.min();
  }

  public String of(Time time) {
    return equals(DISCORD) ? "<t:" + time.epoch() + ":R>" : format().format(time.getTime());
  }
}
