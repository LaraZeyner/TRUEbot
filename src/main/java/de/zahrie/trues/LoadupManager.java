package de.zahrie.trues;

import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.scheduler.ScheduleManager;
import de.zahrie.trues.api.scouting.AnalyzeManager;
import de.zahrie.trues.api.scouting.analyze.RiotPlayerAnalyzer;
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

    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
    final Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.FINE);
    Logger.getAnonymousLogger().addHandler(consoleHandler);

    Database.connect();
    log.info("Datenbank geladen");

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
    reset();
  }

  private void reset() {
    AnalyzeManager.reset();
    RiotPlayerAnalyzer.reset();
    TeamLoader.reset();
    Query.reset();
    ScheduleManager.reset();
  }

  public void askForDisconnect(Boolean restart) {
    if (restart != null) this.restartAfter = restart;
    if (disconnectingMillis != null && Database.connection().isCloseable()) {
      doDisconnect();
    }
  }

  private void doDisconnect() {
    Nunu.getInstance().disconnect();
    Database.disconnect();
    instance = null;
    log.info("System beendet in " + (System.currentTimeMillis() - disconnectingMillis) + " Millisekunden.");
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
