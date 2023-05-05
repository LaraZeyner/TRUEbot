package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;

@Table(value = "coverage_season", department = "pro")
public class ProfessionalSeason extends Season implements Entity<ProfessionalSeason> {
  @Serial
  private static final long serialVersionUID = 8782660232234358661L;

  public ProfessionalSeason(int id, String name, String fullName, TimeRange range, boolean active) {
    super(id, name, fullName, range, active);
  }

  public static ProfessionalSeason get(Object[] objects) {
    return new ProfessionalSeason(
        (int) objects[0],
        (String) objects[2],
        (String) objects[3],
        new TimeRange((LocalDateTime) objects[4], (LocalDateTime) objects[5]),
        (boolean) objects[6]
    );
  }

  @Override
  public ProfessionalSeason create() {
    return new Query<ProfessionalSeason>().key("department", "pro")
        .key("season_name", name).key("season_full", fullName)
        .col("season_start", range.getStartTime()).col("season_end", range.getEndTime()).col("active", active).insert(this);
  }
}
