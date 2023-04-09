package de.zahrie.trues.api.coverage.playday;

import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public class PlaydayFactory {
  public static Playday getPlayday(PlayStage stage, int index) {
    final var playday = QueryBuilder.hql(Playday.class, "FROM Playday WHERE stage = " + stage + " AND idx = " + index).single();
    return Util.avoidNull(playday, new PlaydayCreator(stage, index).create());
  }

  public static Playday fromMatchtime(Stage stage, LocalDateTime start) {
    return QueryBuilder.hql(Playday.class, "FROM Playday WHERE stage = " + stage + " AND startTime = " + start).single();
  }

  @Nullable
  public static Playday current() {
    return QueryBuilder.hql(Playday.class, "FROM Playday WHERE startTime < NOW() ORDER BY startTime DESC LIMIT 1").single();
  }
}
