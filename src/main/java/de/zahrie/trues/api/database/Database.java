package de.zahrie.trues.api.database;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;


public final class Database {
  static DatabaseConnection connection;

  public static DatabaseConnection connection() {
    return connection;
  }

  public static Object insert(Object object) {
    connection().getSession().insert(object);
    return object;
  }

  public static Object update(Object object) {
    try {
      connection().getSession().update(object);
    } catch (Exception exception) {
      insert(object);
    }
    return object;
  }

  public static void remove(Object object) {
    connection.getSession().delete(object);
  }

  /**
   * Speichere Entity und commite, sofern zulässig
   */
  public static Object insertAndCommit(Object object) {
    insert(object);
    connection.getTransaction().commit();
    return object;
  }

  /**
   * Speichere Entity und commite, sofern zulässig
   */
  public static Object updateAndCommit(Object object) {
    update(object);
    connection.getTransaction().commit();
    return object;
  }

  /**
   * Entferne Entity und commite, sofern zulässig
   */
  public static void removeAndCommit(Object object) {
    remove(object);
    connection.commit();
  }

  public static class Connector {
    public static void connect() {
      if (connection != null) return;

      final SessionFactory sessionFactory = new DatabaseEntityRegisterer().register();
      final var session = sessionFactory.openStatelessSession();
      final var transaction = session.beginTransaction();
      connection = new DatabaseConnection(sessionFactory, session, transaction);
    }

    public static void disconnect() {
      connection.getSession().close();
      connection.getSessionFactory().close();
      connection = null;
    }
  }


  public static class Find {
    public static <T> T find(Class<T> entityClass, long id) {
      try {
        return connection.getSession().get(entityClass, id);
      } catch (ClassCastException exception) {
        return connection.getSession().get(entityClass, ((Long) id).intValue());
      }

    }

    public static List<Object[]> findObjectList(String[] params, Object[] values, String query) {
      return performWithNamedQuery(params, values, query).list();
    }

    private static Query<Object[]> performWithNamedQuery(String[] params, Object[] values, String queryName) {
      final var query = Database.connection().getSession().getNamedQuery(queryName);
      for (int i = 0; i < params.length; i++) {
        final String param = params[i];
        final Object value = values[i];
        query.setParameter(param, value);
      }
      return query;
    }

    public static List<Object[]> findObjectListByQuery(String[] params, Object[] values, String query) {
      return performWithNamedQueryByQuery(params, values, query).list();
    }

    private static Query<Object[]> performWithNamedQueryByQuery(String[] params, Object[] values, String queryName) {
      final var query = Database.connection().getSession().createQuery(queryName);
      for (int i = 0; i < params.length; i++) {
        final String param = params[i];
        final Object value = values[i];
        query.setParameter(param, value);
      }
      return query;
    }

    public static <T> List<T> getData(String queryName) {
      return connection.getSession().getNamedQuery(queryName).list();
    }
  }

}
