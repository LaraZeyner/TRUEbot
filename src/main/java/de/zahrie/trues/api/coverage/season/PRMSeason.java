package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "coverage_season", department = "prime")
public class PRMSeason extends Season implements Entity<PRMSeason> {
  @Serial
  private static final long serialVersionUID = -3519857892120876511L;

  private int prmId; // season_id

  public PRMSeason(int id, String name, String fullName, TimeRange range, boolean active, int prmId) {
    super(id, name, fullName, range, active);
    this.prmId = prmId;
  }

  public static PRMSeason get(Object[] objects) {
    return new PRMSeason(
        (int) objects[0],
        (String) objects[2],
        (String) objects[3],
        new TimeRange((LocalDateTime) objects[4], (LocalDateTime) objects[5]),
        (boolean) objects[6],
        (int) objects[7]
    );
  }

  @Override
  public PRMSeason create() {
    return new Query<PRMSeason>().key("department", "prime")
        .key("season_name", name).key("season_full", fullName).key("season_id", prmId)
        .col("season_start", range.getStartTime()).col("season_end", range.getEndTime()).col("active", active).insert(this);
  }
}
