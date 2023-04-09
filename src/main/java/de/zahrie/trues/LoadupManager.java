package de.zahrie.trues;

import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.util.Connectable;
import de.zahrie.trues.util.Const;
import lombok.extern.java.Log;

@Log
public final class LoadupManager implements Connectable {
  private static LoadupManager instance;

  public static LoadupManager getInstance() {
    if (instance == null) instance = new LoadupManager();
    return instance;
  }

  private Long disconnectingMillis = null;
  private boolean restartAfter;
  private long initMillis;

  private LoadupManager() {
    this.initMillis = System.currentTimeMillis();
  }

  /**
   * ueberpruefe Connection
   */
  @Override
  public void connect() {
    if (!Const.check()) System.exit(1);

    Database.Connector.connect();
    log.info("Datenbank geladen");

    Xayah.getInstance().connect();
    log.info("Riot-API geladen");

    Nunu.getInstance().connect();
    log.info("System gestartet in " + (System.currentTimeMillis() - initMillis) + " Millisekunden.");
  }


  @Override
  public void disconnect() {
    this.disconnectingMillis = System.currentTimeMillis();
    askForDisconnect(false);
  }

  public void restart() {
    this.disconnectingMillis = System.currentTimeMillis();
    askForDisconnect(true);
  }

  public void askForDisconnect(Boolean restart) {
    if (restart != null) this.restartAfter = restart;
    if (disconnectingMillis != null && Database.connection().isCloseable()) {
      doDisconnect();
    }
  }

  private void doDisconnect() {
    Xayah.getInstance().disconnect();
    Nunu.getInstance().disconnect();
    Database.Connector.disconnect();
    instance = null;
    log.info("System beendet in " + (System.currentTimeMillis() - disconnectingMillis) + " Millisekunden.");
    System.exit(0);
    handleRestart();
  }

  private void handleRestart() {
    if (restartAfter) {
      this.disconnectingMillis = null;
      this.initMillis = System.currentTimeMillis();
      connect();
    }
  }
// TODO SeasonLoader.load();
}
