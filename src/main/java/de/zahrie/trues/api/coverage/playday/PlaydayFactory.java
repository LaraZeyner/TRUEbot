package de.zahrie.trues.api.coverage.playday;

import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.QueryBuilder;
import org.jetbrains.annotations.Nullable;

public class PlaydayFactory {
  public static Playday getPlayday(PlayStage stage, int index) {
    final var playday = QueryBuilder.hql(Playday.class, "FROM Playday WHERE stage = " + stage.getId() + " AND idx = " + index).single();
    return playday == null ? new PlaydayCreator(stage, index).create() : playday;
  }

  public static Playday fromMatchtime(Stage stage, LocalDateTime start) {
    return QueryBuilder.hql(Playday.class, "FROM Playday WHERE stage = " + stage.getId() + " AND range.startTime = " + start).single();
  }

  @Nullable
  public static Playday current() {
    return QueryBuilder.hql(Playday.class, "FROM Playday WHERE range.startTime < NOW() ORDER BY range.startTime DESC LIMIT 1").single();
  }
}
