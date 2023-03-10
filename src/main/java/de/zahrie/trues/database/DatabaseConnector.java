package de.zahrie.trues.database;

import org.hibernate.SessionFactory;

public class DatabaseConnector {

  public static void connect() {
    final SessionFactory sessionFactory = new DatabaseEntityRegisterer().register();
    final var session = sessionFactory.openSession();
    final var transaction = session.beginTransaction();
    Database.connection = new DatabaseConnection(sessionFactory, session, transaction);
  }

  public static void disconnect() {
    Database.connection.session().close();
    Database.connection.sessionFactory().close();
    Database.connection = null;
  }

}
