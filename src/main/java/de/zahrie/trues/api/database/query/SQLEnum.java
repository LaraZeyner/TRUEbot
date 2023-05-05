package de.zahrie.trues.api.database.query;

import java.util.Arrays;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.util.StringUtils;
import lombok.extern.java.Log;

@Log
public class SQLEnum<E extends Enum<E>> {
  protected Class<E> targetId;

  public E of(Object source) {
    if (source instanceof String sourceString) {
      return toEnum(targetId, sourceString);
    } else if (source instanceof Number sourceInt) {
      return toEnum(targetId, (int) sourceInt);
    }
    throw new IllegalArgumentException("FATAL ERROR");
  }

  static <E extends Enum<E>> E toEnum(Class<E> enumClazz, int index) {
    final E[] enumConstants = enumClazz.getEnumConstants();
    if (index >= enumConstants.length) throw new ArrayIndexOutOfBoundsException("Der Wert ist zu groß.");
    return enumConstants[index];
  }

  static <E extends Enum<E>> E toEnum(Class<E> enumClazz, String source) {
    final Listing.ListingType value = enumClazz.getAnnotation(Listing.class).value();
    return Arrays.stream(enumClazz.getEnumConstants())
        .filter(e -> (switch (value) {
          case CUSTOM -> e.toString();
          case LOWER -> e.toString().toLowerCase();
          case UPPER -> e.toString().toUpperCase();
          case CAPITALIZE -> StringUtils.capitalizeEnum(e.toString().toLowerCase());
          case ORDINAL -> e.ordinal();
        }).equals(source))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("Der Wert kann nicht vergeben werden."));
  }

}
