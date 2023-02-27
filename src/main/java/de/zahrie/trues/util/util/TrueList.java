package de.zahrie.trues.util.util;

import java.util.ArrayList;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
public class TrueList<E> extends ArrayList<E> {

  public TrueList() {
    super();
  }

  public TrueList(@NotNull Collection<? extends E> c) {
    super(c);
  }

  public E firstOrNull() {
    return this.stream().findFirst().orElse(null);
  }

}
