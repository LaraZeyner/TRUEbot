package de.zahrie.trues.util.database;

import org.hibernate.SessionFactory;

public class DatabaseConnector {

  public static void connect() {
    final SessionFactory sessionFactory = FactoryBuilder.build();
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
