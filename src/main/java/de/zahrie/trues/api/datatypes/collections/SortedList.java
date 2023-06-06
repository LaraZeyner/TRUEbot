package de.zahrie.trues.api.datatypes.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.zahrie.trues.api.database.query.Id;
import lombok.NonNull;

public class SortedList<E> extends AbstractList<E> {
  private Set<E> data;
  private boolean sorted = false;
  private final Comparator<? super E> comparator;

  public SortedList() {
    this(new LinkedHashSet<>(), null);
  }

  public SortedList(boolean sorted) {
    this(new LinkedHashSet<>(), null);
    this.sorted = sorted;
  }

  public SortedList(Comparator<? super E> comparator) {
    this(new LinkedHashSet<>(), comparator);
  }

  public SortedList(@NonNull Stream<? extends E> c) {
    this(c.toList(), null);
  }

  public SortedList(Collection<? extends E> c) {
    this(c, null);
  }

  public SortedList(Collection<? extends E> c, boolean sorted) {
    this(c, null);
    this.sorted = sorted;
  }

  public SortedList(Collection<? extends E> collection, Comparator<? super E> comparator) {
    this.data = new LinkedHashSet<>(collection);
    this.comparator = comparator;
  }

  @Override
  public void add(int index, E element) {
    data.add(element);
    if (sorted || comparator != null) sort();
  }

  @Override
  public boolean remove(Object o) {
    return data.remove(o);
  }

  @Override
  public E get(int index) {
    return data.stream().toList().get(index);
  }

  @Override
  public int size() {
    return data.size();
  }

  private void sort() {
    if (size() == 0) return;
    if (comparator != null) this.data = data.stream().sorted(comparator).collect(Collectors.toCollection(LinkedHashSet::new));
    else if (get(0) instanceof Comparable) this.data = new LinkedHashSet<>(new TreeSet<>(data));
    else if (get(0) instanceof Id) {
      this.data = data.stream().sorted(Comparator.comparingInt(o -> ((Id) o).getId())).collect(Collectors.toCollection(LinkedHashSet::new));
    }
  }

  public SortedList<E> reverse() {
    final ArrayList<E> list = new ArrayList<>(data);
    Collections.reverse(list);
    this.data = new LinkedHashSet<>(list);
    return this;
  }

  @Override
  public void clear() {
    data.clear();
  }
}
