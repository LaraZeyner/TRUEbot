package de.zahrie.trues.util.database;

import de.zahrie.trues.util.logger.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public record DatabaseConnection(SessionFactory sessionFactory, Session session, Transaction transaction) {

  public void commit() {
    try {
      transaction.commit();
      transaction.begin();
    } catch (Exception e) {
      transaction.rollback();
      Logger.getLogger("Database").severe("Error saving. Transaction has been rolled back.");
      throw new RuntimeException(e);
    }
  }

}
