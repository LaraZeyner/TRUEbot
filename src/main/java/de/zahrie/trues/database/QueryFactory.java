package de.zahrie.trues.database;

import org.hibernate.query.Query;

public class QueryFactory {
  static <T> T performSingle(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    try {
      final var query = performWithNamedQuery(entityClass, params, values, subquery);
      return query.setMaxResults(1).getSingleResult();
    } catch (ClassCastException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  static Query<Object[]> performWithNamedQuery(String[] params, Object[] values, String queryName) {
    final var query = Database.connection().getSession().getNamedQuery(queryName);
    for (int i = 0; i < params.length; i++) {
      final String param = params[i];
      final Object value = values[i];
      query.setParameter(param, value);
    }
    return query;
  }

  static <T> Query<T> performWithNamedQuery(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    final var entityClassName = EntityFactory.getName(entityClass);
    return (Query<T>) performWithNamedQuery(params, values, entityClassName + "." + subquery);
  }

  static <T> T performSingleQueryString(String queryString, String[] params, Object[] values, Class<T> entityClass) {
    try {
      final var query = performWithQueryString(queryString, params, values, entityClass);
      return query.setMaxResults(1).getSingleResult();
    } catch (ClassCastException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  static Query<Object[]> performWithQueryString(String queryString, String[] params, Object[] values) {
    final var query = Database.connection().getSession().createQuery(queryString);
    for (int i = 0; i < params.length; i++) {
      final String param = params[i];
      final Object value = values[i];
      query.setParameter(param, value);
    }
    return query;
  }

  static <T> Query<T> performWithQueryString(String queryString, String[] params, Object[] values, Class<T> entityClass) {
    final var entityClassName = EntityFactory.getName(entityClass);
    return (Query<T>) performWithQueryString(queryString, params, values);
  }
}
