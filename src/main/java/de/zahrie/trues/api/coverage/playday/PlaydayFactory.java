package de.zahrie.trues.api.coverage.playday;

import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.query.Query;
import org.jetbrains.annotations.Nullable;

public class PlaydayFactory {
  public static Playday getPlayday(Stage stage, int index) {
    final var playday = new Query<Playday>().where("stage", stage).and("playday_index", index).entity();
    return playday == null ? new PlaydayCreator(stage, index).create() : playday;
  }

  public static Playday fromMatchtime(Stage stage, LocalDateTime start) {
    return new Query<Playday>().where("stage", stage).where("playday_start", start).entity();
  }

  @Nullable
  public static Playday current() {
    return new Query<Playday>().where("playday_start <= NOW()").descending("playday_start").entity();
  }
}
