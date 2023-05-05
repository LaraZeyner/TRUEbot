package de.zahrie.trues.api.coverage.playday;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Comparator;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.config.PlaydayRange;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("coverage_playday")
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

  public static Playday get(Object[] objects) {
    return new Playday(
        (int) objects[0],
        new Query<Stage>().entity(objects[1]),
        (short) objects[2],
        new TimeRange((LocalDateTime) objects[3], (LocalDateTime) objects[4]),
        new SQLEnum<MatchFormat>().of(objects[5])
    );
  }

  @Override
  public Playday create() {
    return new Query<Playday>()
        .key("stage", stage).key("playday_index", idx)
        .col("playday_start", range.getStartTime()).col("playday_end", range.getEndTime()).col("format", format)
        .insert(this);
  }

  @Override
  public int compareTo(@NotNull Playday o) {
    return Comparator.comparing(Playday::getRange).compare(this, o);
  }
}
