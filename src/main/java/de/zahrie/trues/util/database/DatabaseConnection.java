package de.zahrie.trues.util.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public record DatabaseConnection(SessionFactory sessionFactory, Session session, Transaction transaction) {

  public void commit() {
    transaction.commit();
    transaction.begin();
  }

}
