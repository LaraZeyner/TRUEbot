package de.zahrie.trues.api.coverage.playday;

import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.Database;

public class PlaydayFactory {
  public static Playday getPlayday(PlayStage stage, int index) {
    final var playday = Database.Find.find(Playday.class, new String[]{"stage", "idx"}, new Object[]{stage, index}, "fromStageAndId");
    return playday != null ? playday : new PlaydayCreator(stage, index).create();
  }

  public static Playday fromMatchtime(Stage stage, Time start) {
    return Database.Find.find(Playday.class, new String[]{"stage", "start"}, new Object[]{stage, start}, "fromStageAndStart");
  }

  public static Playday current() {
    //TODO (Abgie) 27.03.2023:
  }
}
