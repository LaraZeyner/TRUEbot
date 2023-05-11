package de.zahrie.trues.api.database.connector;

public final class SQLUtils {
  public static Byte byteValue(Object object) {
    if (object == null) return null;
    return ((Number) object).byteValue();
  }

  public static Short shortValue(Object object) {
    if (object == null) return null;
    return ((Number) object).shortValue();
  }
}
