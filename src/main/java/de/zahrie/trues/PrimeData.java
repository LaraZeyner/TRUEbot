package de.zahrie.trues;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.util.database.Database;
import de.zahrie.trues.util.database.DatabaseConnector;
import de.zahrie.trues.util.logger.Logger;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

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

  private Team trueTeam;

  public PrimeData() {
    DatabaseConnector.connect();
  }

  private void init() {
    if (this.trueTeam == null) {
      val logger = Logger.getLogger("Init");
      this.trueTeam = Database.Find.find(Team.class, 142116);
      logger.info("Datenbank geladen");
    }
  }

}
