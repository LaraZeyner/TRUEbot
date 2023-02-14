package de.zahrie.trues.util.structures;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import static de.zahrie.trues.util.Util.cast;

/**
 * Created by Lara on 13.02.2023 for TRUEbot
 */
@NoArgsConstructor
public class MaxList<T extends Number> extends ArrayList<T> {
  @Override
  public void add(int index, Number number) {
    if (size() < index) {
      throw new IndexOutOfBoundsException("Teile der Liste sind leer.");
    }

    if (size() == index) {
      super.add(cast(number));
      return;
    }

    if (get(index).doubleValue() < number.doubleValue()) {
      super.set(index, cast(number));
    }
  }

}
