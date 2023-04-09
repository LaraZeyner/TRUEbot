package de.zahrie.trues.api.database;

import java.util.List;

import jakarta.persistence.metamodel.EntityType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;


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
    public static <T> String getEntityName(Class<T> entityClass) {
      final Session session = connection().getSession();
      final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
      return storedEntity.getName();
    }

    public static <T> T find(Class<T> entityClass, long id) {
      return connection.getSession().get(entityClass, id);
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

    public static <T> List<T> getData(String queryName) {
      return connection.getSession().getNamedQuery(queryName).list();
    }
  }

}
