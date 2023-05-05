package de.zahrie.trues.api.database.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.zahrie.trues.util.io.cfg.JSON;
import org.json.JSONObject;


public final class Database {
  static DatabaseConnection connection;

  public static DatabaseConnection connection() {
    return connection;
  }

  public static class Connector {
    public static void connect() {
      connection = Connector.run();
    }

    static DatabaseConnection run() {
      final var json = JSON.fromFile("connect.json");
      final JSONObject dbObject = json.getJSONObject("database");
      final String database = dbObject.getString("database");
      final String password = dbObject.getString("password");
      final int port = dbObject.getInt("port");
      final String server = dbObject.getString("server");
      final String username = dbObject.getString("username");

      try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        final String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?sessionVariables=&&sql_mode=''";
        final Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
        return new DatabaseConnection(connection);
      } catch (ClassNotFoundException | SQLException e) {
        throw new RuntimeException(e);
      }
    }

    public static void disconnect() {
      connection.commit();
      try {
        connection.getConnection().close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      connection = null;
    }
  }
}
