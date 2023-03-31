package de.zahrie.trues.database;

import java.util.List;

import jakarta.persistence.NoResultException;
import org.hibernate.SessionFactory;

public final class Database {
  static DatabaseConnection connection;

  public static DatabaseConnection connection() {
    return connection;
  }

  public static void save(Object object) {
    connection.getSession().merge(object);
  }

  public static void remove(Object object) {
    connection.getSession().remove(object);
  }

  public static void saveAndCommit(Object object) {
    save(object);
    connection.commit();
  }

  public static void removeAndCommit(Object object) {
    remove(object);
    connection.commit();
  }

  public static void flush() {
    connection.getSession().flush();
  }

  public static class Connector {
    public static void connect() {
      if (connection != null) return;

      final SessionFactory sessionFactory = new DatabaseEntityRegisterer().register();
      final var session = sessionFactory.openSession();
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
    public static <T> List<T> findList(Class<T> entityClass) {
      final String entityClassName = EntityFactory.getName(entityClass);
      return getData(entityClassName + ".findAll");
    }

    public static <T> List<T> getData(String queryName) {
      return connection.getSession().getNamedQuery(queryName).list();
    }

    public static <T> List<T> findList(Class<T> entityClass, String subQuery) {
      return findList(entityClass, new String[]{}, new Object[]{}, subQuery);
    }

    public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values) {
      return findList(entityClass, params, values, "findBy");
    }

    public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
      return QueryFactory.performWithSubquery(entityClass, params, values, subQuery).list();
    }

    public static List<Object[]> findObjectList(String[] params, Object[] values, String query) {
      return QueryFactory.performWithSubquery(params, values, query).list();
    }

    public static <T> T find(Class<T> entityClass, long id) {
      return connection.getSession().get(entityClass, id);
    }

    public static <T> T find(Class<T> entityClass, String subQuery) {
      return find(entityClass, new String[]{}, new Object[]{}, subQuery);
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
