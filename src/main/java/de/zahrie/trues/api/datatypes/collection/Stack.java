package de.zahrie.trues.api.datatypes.collection;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
public class Stack<E> extends ArrayList<E> {
  @Serial
  private static final long serialVersionUID = 3877927374583616003L;

  public Stack() {
    super();
  }

  public Stack(@NotNull Collection<? extends E> c) {
    super(c);
  }

  public E firstOrNull() {
    return this.stream().findFirst().orElse(null);
  }

}
