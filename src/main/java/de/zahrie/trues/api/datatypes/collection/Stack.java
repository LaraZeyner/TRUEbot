package de.zahrie.trues.api.datatypes.collection;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

public class Stack<E> extends ArrayList<E> {
  @Serial
  private static final long serialVersionUID = 3877927374583616003L;

  public Stack() {
    super();
  }

  public Stack(@NotNull Collection<? extends E> c) {
    // TODO (Abgie) 15.03.2023: never used
    super(c);
  }

  public E firstOrNull() {
    // TODO (Abgie) 15.03.2023: never used
    return this.stream().findFirst().orElse(null);
  }

}
