package de.zahrie.trues.api.coverage.playday;

import java.util.Calendar;

import de.zahrie.trues.api.coverage.stage.Stage;
import de.zahrie.trues.util.database.Database;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class PlaydayFactory {
  public static Playday getPlayday(Stage stage, int index) {
    Playday playday = Database.Find.find(Playday.class, new String[]{"stage", "idx"}, new Object[]{stage, index}, "fromStageAndId");
    if (playday != null) {
      return playday;
    }
    if (stage.getName().equals("Gruppenphase")) {
      playday = new Playday(stage, (short) index);
      Database.save(playday);
      return playday;
    }
    // TODO add Playday for Calibration and Playoffs
    return null;
  }

  public static Playday fromMatchtime(Stage stage, Calendar calendar) {
    return Database.Find.find(Playday.class, new String[]{"stage", "start"}, new Object[]{stage, calendar}, "fromStageAndStart");
  }
}
