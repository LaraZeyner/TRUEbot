package de.zahrie.trues;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.database.DatabaseConnector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
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
      // this.trueTeam = Database.Find.find(Team.class, 142116);
      log.info("Datenbank geladen");
    }
  }

}
