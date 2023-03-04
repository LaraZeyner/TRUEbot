package de.zahrie.trues.database;

import org.hibernate.query.Query;

/**
 * Created by Lara on 26.02.2023 for TRUEbot
 */
public class QueryFactory {
  static <T> T performSingle(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    try {
      final var query = performWithSubquery(entityClass, params, values, subquery);
      return query.setMaxResults(1).getSingleResult();
    } catch (ClassCastException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  static Query<Object[]> performWithSubquery(String[] params, Object[] values, String queryName) {
    final var query = Database.connection().session().getNamedQuery(queryName);
    for (int i = 0; i < params.length; i++) {
      final String param = params[i];
      final Object value = values[i];
      query.setParameter(param, value);
    }
    return query;
  }

  static <T> Query<T> performWithSubquery(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    final var entityClassName = EntityFactory.getName(entityClass);
    return (Query<T>) performWithSubquery(params, values, entityClassName + "." + subquery);
  }
}
