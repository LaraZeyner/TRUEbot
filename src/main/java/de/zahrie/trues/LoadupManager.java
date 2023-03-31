package de.zahrie.trues;

import java.util.Calendar;

import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Connectable;
import de.zahrie.trues.util.Const;
import lombok.extern.java.Log;

@Log
public class LoadupManager implements Connectable {
  private static LoadupManager instance;

  public static LoadupManager getInstance() {
    if (instance == null) instance = new LoadupManager();
    return instance;
  }


  private Calendar init;

  private LoadupManager() {
    this.init = Calendar.getInstance();
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
    log.info("System gestartet in " + (Calendar.getInstance().getTimeInMillis() - init.getTimeInMillis()) + " Millisekunden.");
  }


  @Override
  public void disconnect() {
    final Calendar disconnectingTime = Calendar.getInstance();
    Nunu.getInstance().disconnect();
    Xayah.getInstance().disconnect();
    Database.Connector.disconnect();
    instance = null;
    log.info("System beendet in " + (Calendar.getInstance().getTimeInMillis() - disconnectingTime.getTimeInMillis()) + " Millisekunden.");
  }

  public void restart() {
    disconnect();
    this.init = Calendar.getInstance();
    connect();
  }


/*
  public static boolean loadRiotData() {
    val logger = Logger.getLogger("Riot-Data");

    ChampionLoader.createChampions();
    logger.info("Champions geladen");

    return true;
  }

  public static boolean loadPrimeLeague() {
    val logger = Logger.getLogger("Prime-League");
    SeasonLoader.load();
    PrimeData.getInstance().commit();
    logger.info("Season geladen");

    PlayerLoader.loadAll();
    PrimeData.getInstance().commit();
    logger.info("Spielerdaten & Spiele geladen");
    // Search for players of Team without account
    AccountLoader.load();
    PrimeData.getInstance().commit();
    // Search for valueable Teams
    AccountLoader.updateTeams();
    PrimeData.getInstance().commit();

    TeamLoader.handleTeam(Const.TEAMID);
    ScheduleLoader.load();
    PrimeData.getInstance().commit();
    return true;
  }

  public static boolean loadGames() {
    val logger = Logger.getLogger("Spielanalyse");
    final Set<Team> teams = PrimeData.getInstance().getCurrentGroup().getTeams();
    final List<ScheduledGame> tourneys = ScheduledGame.findMode(QueueType.TOURNEY);
    // CompGames der Ligateams
    tourneys.stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream().anyMatch(teams::contains)).forEach(RiotGameRequester::loadCompetitive);
    logger.info("CompGames der Ligateams geladen");


    // Games der nächsten Gegner + TRUES mit 3
    List<Team> teams1 = Arrays.asList(Team.findTid(Const.TEAMID), Schedule.nextOrLast().getEnemyTeam());
    final Query<ScheduledGame> query = PrimeData.getInstance().getSession().createQuery("FROM ScheduledGame s " +
        "WHERE teams LIKE :t1 OR teams LIKE :t2");
    query.setParameter("t1", "%" + teams1.get(0).getId() + "%");
    query.setParameter("t2", teams1.size() > 1 ? ("%" + teams1.get(1).getId() + "%") : ("%" + teams1.get(0).getId() + "%"));
    final List<ScheduledGame> list = query.list();
    list.stream().filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.CLASH))
        .filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(team -> teams1.contains(team) && scheduledGame.getTeamsMap().get(team) > 2))
        .forEach(RiotGameRequester::loadClashGame);
    list.stream().filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.OTHER))
        .filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
            .anyMatch(team -> teams1.contains(team) && scheduledGame.getTeamsMap().get(team) > 2))
        .forEach(RiotGameRequester::loadMatchmade);
    logger.info("Games der nächsten Gegner + TRUES mit 3 geladen");

    // Clash der nächsten Gegner + TRUES
    ScheduledGame.findMode(QueueType.CLASH).stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(teams1::contains)).forEach(RiotGameRequester::loadClashGame);
    logger.info("Clash der nächsten Gegner + TRUES geladen");

    // Games der nächsten Gegner + TRUE
    final Query<ScheduledGame> query2 = PrimeData.getInstance().getSession().createQuery("FROM ScheduledGame s " +
        "WHERE teams LIKE :t1 OR teams LIKE :t2");
    query2.setParameter("t1", "%" + teams1.get(0).getId() + "%");
    query2.setParameter("t2", teams1.size() > 1 ? ("%" + teams1.get(1).getId() + "%") : ("%" + teams1.get(0).getId() + "%"));
    final List<ScheduledGame> list2 = query2.list();
    list2.stream().filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.OTHER)).forEach(RiotGameRequester::loadMatchmade);

    logger.info("Games der nächsten Gegner + TRUES geladen");

    // Alle Tourney
    ScheduledGame.findMode(QueueType.TOURNEY).forEach(RiotGameRequester::loadCompetitive);
    logger.info("Tourney geladen");

    // Alle Games mit 3
    ScheduledGame.findMode(QueueType.CLASH).stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(team -> scheduledGame.getTeamsMap().get(team) > 2)).forEach(RiotGameRequester::loadClashGame);
    ScheduledGame.findMode(QueueType.OTHER).stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(team -> scheduledGame.getTeamsMap().get(team) > 2)).forEach(RiotGameRequester::loadMatchmade);
    logger.info("Games mit 3 geladen");

    // Alle Clash
    ScheduledGame.findMode(QueueType.CLASH).forEach(RiotGameRequester::loadClashGame);
    logger.info("Clash geladen");

    // Alle Games
    ScheduledGame.findMode(QueueType.OTHER).forEach(RiotGameRequester::loadMatchmade);

    return true;
  }


 */
}
