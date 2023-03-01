package de.zahrie.trues.util.util;

import java.io.Serial;

import lombok.AllArgsConstructor;

/**
 * Created by Lara on 27.02.2023 for TRUEbot
 */
@AllArgsConstructor
public class TrueNumber extends Number {
  @Serial
  private static final long serialVersionUID = 7454286529912546971L;
  private Double value;

  @Override
  public int intValue() {
    return value.intValue();
  }

  @Override
  public long longValue() {
    return value.longValue();
  }

  @Override
  public float floatValue() {
    return value.floatValue();
  }

  @Override
  public double doubleValue() {
    return value;
  }

  @Override
  public String toString() {
    if (hasDigits()) {
      return String.valueOf(value);
    }
    return String.valueOf(value.longValue());
  }

  public boolean hasDigits() {
    return value % 1 != 0;
  }

  public TrueNumber roundValue(int digits) {
    final long round = Math.round(value * Math.pow(10, digits));
    return new TrueNumber(round / Math.pow(10, digits));
  }

  public String roundValue() {
    if (hasDigits() && value <= 1) {
      return percentValue();
    }
    return "" + roundValue(0).intValue();
  }

  public String percentValue() {
    if (value < 0.01) {
      return String.valueOf(value * 100).substring(1, 4) + "%";
    }
    if (value < 0.1) {
      return String.valueOf(value * 100).substring(0, 3) + "%";
    }
    return roundValue(2).intValue() + "%";
  }

  public TrueNumber div(double divisor) {
    return div(divisor, 0);
  }

  public TrueNumber div(double divisor, boolean re) {
    return re ? div(divisor, value) : div(divisor, 0);
  }

  public TrueNumber div(double divisor, double result) {
    if (divisor == 0) {
      return new TrueNumber(result);
    }
    return new TrueNumber(value / divisor);
  }

}
