package de.zahrie.trues.api.datatypes.number;

import java.io.Serial;

public class TrueNumber extends Calculateable<TrueNumber> {
  @Serial
  private static final long serialVersionUID = 7454286529912546971L;

  public TrueNumber(Double value) {
    super(TrueNumber.class, value);
  }

  public TrueNumber(int value) {
    this((double) value);
  }

  @Override
  public String toString() {
    return hasDigits() ? String.valueOf(value) : String.valueOf(value.longValue());
  }

  public boolean hasDigits() {
    return value % 1 != 0;
  }

  public String round() {
    return hasDigits() && value <= 1 ? percentValue() : "" + round(0).intValue();
  }

  public String percentValue() {
    if (value < 0.01) {
      return String.valueOf(value * 100).substring(1, 4) + "%";
    }
    if (value < 0.1) {
      return String.valueOf(value * 100).substring(0, 3) + "%";
    }
    return round(2).intValue() + "%";
  }

  public TrueNumber round(int digits) {
    final long round = Math.round(value * Math.pow(10, digits));
    return new TrueNumber(round / Math.pow(10, digits));
  }

}
