package de.zahrie.trues.api.database.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.util.io.cfg.JSON;
import de.zahrie.trues.util.io.log.Console;
import de.zahrie.trues.util.io.log.DevInfo;
import org.json.JSONObject;


public final class Database {
  static DatabaseConnection connection;

  public static DatabaseConnection connection() {
    return connection;
  }

  public static class Connector {
    public static void connect() {
      connection = Connector.run();
      new Query<>(DiscordUser.class).col("joined", null).update(List.of());
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
        final String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?sessionVariables=sql_mode=''";
        final Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
        return new DatabaseConnection(connection);
      } catch (ClassNotFoundException | SQLException e) {
        new Console("SQL konnte nicht gefunden werden").severe(e);
        throw new RuntimeException(e);
      }
    }

    public static void disconnect() {
      connection.commit();
      try {
        connection.getConnection().close();
      } catch (SQLException e) {
        new DevInfo("SQL-Connection konnte nicht geschlossen werden").severe(e);
        throw new RuntimeException(e);
      }
      connection = null;
    }
  }
}
