package de.zahrie.trues.api.database.connector;

import java.sql.Connection;
import java.sql.SQLException;

import de.zahrie.trues.LoadupManager;
import lombok.Data;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
@Data
public class DatabaseConnection {
  private final Connection connection;

  private Boolean commitable = true;

  public Boolean isCloseable() {
    return commitable;
  }

  public void commit(@Nullable Boolean commitable) {
    if (commitable == null) {
      commit(true);
      this.commitable = false;
      return;
    }
    this.commitable = commitable;
    commit();
    LoadupManager.getInstance().askForDisconnect(null);
  }

  public void commit() {
    if (commitable) forceCommit();
  }

  public void forceCommit() {
    try {
      connection.commit();
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    }
  }
}
