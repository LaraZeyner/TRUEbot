package de.zahrie.trues.api.datatypes.number;

import java.io.Serial;

import org.jetbrains.annotations.NotNull;

public abstract class Numeric<E extends Number> extends Number implements Comparable<E> {
  @Serial
  private static final long serialVersionUID = -8121219205387485534L;


  protected Double value;

  public Numeric(Double value) {
    this.value = value;
  }

  @Override
  public int compareTo(@NotNull E o) {
    return Double.compare(doubleValue(), o.doubleValue());
  }

  @Override
  public int intValue() {
    return 0;
  }

  @Override
  public long longValue() {
    return 0;
  }

  @Override
  public float floatValue() {
    return 0;
  }

  @Override
  public double doubleValue() {
    return 0;
  }
}
