package de.zahrie.trues.database;

import lombok.Data;
import lombok.extern.java.Log;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;

@Log
@Data
public class DatabaseConnection {
  private final SessionFactory sessionFactory;
  private final Session session;
  private final Transaction transaction;

  private Boolean commitable = true;

  public void commit(@Nullable Boolean commitable) {
    if (commitable == null) {
      commit(true);
      this.commitable = false;
      return;
    }
    this.commitable = commitable;
    commit();
  }

  public void commit() {
    if (commitable) forceCommit();
  }

  public void forceCommit() {
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
