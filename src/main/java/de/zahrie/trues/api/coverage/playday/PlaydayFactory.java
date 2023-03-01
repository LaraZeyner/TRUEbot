package de.zahrie.trues.api.coverage.playday;

import java.util.Calendar;

import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.util.database.Database;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class PlaydayFactory {
  public static Playday getPlayday(PlayStage stage, int index) {
    final var playday = Database.Find.find(Playday.class, new String[]{"stage", "idx"}, new Object[]{stage, index}, "fromStageAndId");
    return playday != null ? playday : new PlaydayCreator(stage, index).create();
  }

  public static Playday fromMatchtime(Stage stage, Calendar calendar) {
    return Database.Find.find(Playday.class, new String[]{"stage", "start"}, new Object[]{stage, calendar}, "fromStageAndStart");
  }
}
