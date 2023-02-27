package de.zahrie.trues.util.database;

import java.util.List;

import jakarta.persistence.NoResultException;

public final class Database {
  static DatabaseConnection connection;

  public static DatabaseConnection connection() {
    if (connection == null) {
      DatabaseConnector.connect();
    }
    return connection;
  }

  public static void save(Object object) {
    connection.session().merge(object);
  }

  public static void remove(Object object) {
    connection.session().remove(object);
  }

  public static void flush() {
    connection.session().flush();
  }

  public static class Find {
    public static <T> List<T> findList(Class<T> entityClass) {
      final String entityClassName = EntityFactory.getName(entityClass);
      return getData(entityClassName + ".findAll");
    }

    public static <T> List<T> getData(String queryName) {
      return connection.session().getNamedQuery(queryName).list();
    }

    public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values) {
      return findList(entityClass, params, values, "findBy");
    }

    public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
      return QueryFactory.performWithSubquery(entityClass, params, values, subQuery).list();
    }

    public static List<Object[]> findObjectList(String[] params, Object[] values, String subQuery) {
      return QueryFactory.performWithSubquery(params, values, subQuery).list();
    }

    public static <T> T find(Class<T> entityClass, long id) {
      return connection.session().get(entityClass, id);
    }

    public static <T> T find(Class<T> entityClass, String[] params, Object[] values) {
      return find(entityClass, params, values, "findBy");
    }

    public static <T> T find(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
      try {
        return QueryFactory.performSingle(entityClass, params, values, subQuery);
      } catch (NoResultException exception) {
        return null;
      }
    }
  }

}
