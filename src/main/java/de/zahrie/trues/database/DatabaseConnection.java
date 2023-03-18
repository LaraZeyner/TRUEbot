package de.zahrie.trues.database;

import lombok.extern.java.Log;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Log
public record DatabaseConnection(SessionFactory sessionFactory, Session session, Transaction transaction) {

  public void commit() {
    try {
      transaction.commit();
      transaction.begin();
    } catch (Exception e) {
      transaction.rollback();
      log.severe("Error saving. Transaction has been rolled back.");
      log.throwing(getClass().getName(), "commit", e);
      throw new RuntimeException(e);
    }
  }

}
