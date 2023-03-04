package de.zahrie.trues.api.datatypes.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.util.logger.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum TimeFormat {
  DEFAULT("E, d. MMM YYYY HH:mm");

  private final String format;

  private SimpleDateFormat format() {
    return new SimpleDateFormat(this.format);
  }

  public Chain now() {
    return this.of(new Time());
  }

  public Time of(Chain dateString) {
    try {
      return Time.of(format().parse(dateString.toString()));
    } catch (ParseException parseException) {
      Logger.getLogger().severe("Fehler", parseException);
    }
    return Time.min();
  }

  public Chain of(Time time) {
    return Chain.of(format().format(time.getTime()));
  }
}
