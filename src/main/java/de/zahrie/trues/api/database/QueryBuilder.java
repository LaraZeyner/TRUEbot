package de.zahrie.trues.api.database;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.query.Query;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Log
public final class QueryBuilder<T> {
  private final Query<T> query;

  public static <T> QueryBuilder<T> named(Class<T> entityClass, String subQuery) {
    final var entityClassName = Database.Find.getEntityName(entityClass);
    final var query = Database.connection().getSession().createNamedQuery(entityClassName + "." + subQuery, entityClass);
    return new QueryBuilder<>(query);
  }

  public static <T> QueryBuilder<T> sql(Class<T> entityClass, String queryString) {
    final var query = Database.connection().getSession().createNativeQuery(queryString, entityClass);
    return new QueryBuilder<>(query);
  }

  public static <T> QueryBuilder<T> hql(Class<T> entityClass, String queryString) {
    final var query = Database.connection().getSession().createQuery(queryString, entityClass);
    return new QueryBuilder<>(query);
  }

  public QueryBuilder<T> addParameter(String key, Object value) {
    query.setParameter(key, value);
    return this;
  }

  public QueryBuilder<T> addParameters(Map<String, Object> params) {
    params.forEach(this::addParameter);
    return this;
  }

  public T single() {
    return query.setMaxResults(1).getSingleResult();
  }

  public List<T> list() {
    return query.list();
  }

  public List<T> list(int amount) {
    return query.setMaxResults(amount).list();
  }

  public List<String> transform() {
    return transform(50, 3);

  }

  public List<String> transform(int rows) {
    return transform(rows, 3);

  }

  public List<String> transform(int rows, int columns) {
    final String[] output = new String[columns];
    final List<T> data = query.setMaxResults(rows).list();
    if (data.isEmpty() || !(data.get(0) instanceof DTO dataDTO)) return List.of("keine Daten", "keine Daten", "keine Daten");
    for (int i = 0; i < dataDTO.getData().size(); i++) {
      if (i >= output.length) continue;
      final int finalI = i;
      output[i] = (data.stream().map(dto -> (DTO) dto).map(dto -> dto.getData().get(finalI)).collect(Collectors.joining("\n")));
    }
    return Arrays.stream(output).toList();
  }
}
