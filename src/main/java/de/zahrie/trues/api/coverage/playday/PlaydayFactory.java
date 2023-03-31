package de.zahrie.trues.api.coverage.playday;

import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public class PlaydayFactory {
  public static Playday getPlayday(PlayStage stage, int index) {
    final var playday = Database.Finder.find("FROM Playday WHERE stage = " + stage + " AND idx = " + index, Playday.class);
    return Util.avoidNull(playday, new PlaydayCreator(stage, index).create());
  }

  public static Playday fromMatchtime(Stage stage, Time start) {
    return Database.Finder.find("FROM Playday WHERE stage = " + stage + " AND startTime = " + start, Playday.class);
  }

  @Nullable
  public static Playday current() {
    return Database.Finder.find("FROM Playday WHERE startTime < NOW() ORDER BY startTime DESC LIMIT 1", Playday.class);
  }
}
