package de.zahrie.trues;

import de.zahrie.trues.models.team.Team;
import de.zahrie.trues.util.database.Database;
import de.zahrie.trues.util.logger.Logger;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
@Getter
@Setter
public class PrimeData {
  private static PrimeData primeData;

  public static PrimeData getInstance() {
    if (primeData == null) {
      primeData = new PrimeData();
      primeData.init();
    }
    return primeData;
  }

  private final Session session;
  private final Transaction transaction;
  private Team trueTeam;

  public PrimeData() {
    this.session = Database.getSessionFactory().openSession();
    this.transaction = session.beginTransaction();
  }

  private void init() {
    if (this.trueTeam == null) {
      Logger logger = Logger.getLogger("Init");
      this.trueTeam = Database.find(Team.class, 142116);
      logger.info("Gruppe geladen");

      //val season = Season.current();
      //primeData.setCurrentSeason(season);
      //primeData.setTrueTeam(team);
      logger.info("Season geladen");
      logger.info("Datenbank geladen");
    }
  }

  public void commit() {
    transaction.commit();
    transaction.begin();
  }

  public void save(Object object) {
    session.saveOrUpdate(object);
  }

  public void remove(Object object) {
    session.remove(object);
  }

  public void flush() {
    session.flush();
  }

  // </editor-fold>

}
