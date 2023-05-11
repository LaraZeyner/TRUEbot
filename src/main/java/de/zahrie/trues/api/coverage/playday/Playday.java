package de.zahrie.trues.api.coverage.playday;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.config.PlaydayRange;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("coverage_playday")
@ExtensionMethod(SQLUtils.class)
public class Playday implements Entity<Playday>, Comparable<Playday> {
  @Serial
  private static final long serialVersionUID = 341434050654966994L;
  private int id;
  private final Stage stage; // stage
  private final short idx; // playday_index
  private final TimeRange range; // playday_start, playday_end
  private final MatchFormat format; // format

  public Playday(Stage stage, short index, PlaydayRange playdayRange, MatchFormat format) {
    this.stage = stage;
    this.idx = index;
    this.range = playdayRange;
    this.format = format;
  }

  public static Playday get(List<Object> objects) {
    return new Playday(
        (int) objects.get(0),
        new Query<>(Stage.class).entity(objects.get(1)),
        objects.get(2).shortValue(),
        new TimeRange((LocalDateTime) objects.get(3), (LocalDateTime) objects.get(4)),
        new SQLEnum<>(MatchFormat.class).of(objects.get(5))
    );
  }

  @Override
  public Playday create() {
    return new Query<>(Playday.class)
        .key("stage", stage).key("playday_index", idx)
        .col("playday_start", range.getStartTime()).col("playday_end", range.getEndTime()).col("format", format)
        .insert(this);
  }

  @Override
  public int compareTo(@NotNull Playday o) {
    return Comparator.comparing(Playday::getRange).compare(this, o);
  }
}
