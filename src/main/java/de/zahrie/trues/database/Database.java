package de.zahrie.trues.database;

import java.util.List;

import de.zahrie.trues.util.Util;
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

  public static class Finder {
    public static <T> List<T> getData(String queryString) {
      return connection.getSession().createQuery(queryString).list();
    }

    public static <T> List<T> findList(String queryString, Class<T> entityClass) {
      return findList(queryString, new String[]{}, new Object[]{}, entityClass);
    }

    public static <T> List<T> findList(String queryString, String[] params, Object[] values, Class<T> entityClass) {
      return QueryFactory.performWithQueryString(queryString, params, values, entityClass).list();
    }

    public static List<Object[]> findObjectList(String queryString, String[] params, Object[] values) {
      return QueryFactory.performWithQueryString(queryString, params, values).list();
    }

    public static <T> T find(String queryString, Class<T> entityClass) {
      return find(queryString, new String[]{}, new Object[]{}, entityClass);
    }

    public static <T> T find(String queryString, String[] params, Object[] values, Class<T> entityClass) {
      try {
        return QueryFactory.performSingleQueryString(queryString, params, values, entityClass);
      } catch (NoResultException exception) {
        return null;
      }
    }

    public static Object[] findObject(String queryString, String[] params, Object[] values) {
      return Util.avoidNull(findObjectList(queryString, params, values).get(0), null);
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
      return QueryFactory.performWithNamedQuery(entityClass, params, values, subQuery).list();
    }

    public static List<Object[]> findObjectList(String[] params, Object[] values, String query) {
      return QueryFactory.performWithNamedQuery(params, values, query).list();
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

    public static Object[] findObject(String[] params, Object[] values, String subQuery) {
      return Util.avoidNull(findObjectList(params, values, subQuery).get(0), null);
    }
  }

}
