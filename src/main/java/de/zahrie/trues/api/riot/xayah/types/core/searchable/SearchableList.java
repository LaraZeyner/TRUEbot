package de.zahrie.trues.api.riot.xayah.types.core.searchable;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Predicate;

public interface SearchableList<T> extends SearchableObject, List<T>, Serializable {
    void delete(Object query);

    void delete(Predicate<T> predicate);

    SearchableList<T> filter(Predicate<T> predicate);

    SearchableList<T> filter(Predicate<T> predicate, boolean streaming);

    T find(Object query);

    T find(Predicate<T> predicate);

    SearchableList<T> search(Object query);

    SearchableList<T> search(Object query, boolean streaming);
}
