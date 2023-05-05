package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;

@Table(value = "coverage_stage", department = "Anmeldung")
public class SignupStage extends Stage implements Entity<SignupStage>, WaitingStage {
  @Serial
  private static final long serialVersionUID = -4885691038267717445L;

  public SignupStage(Season season, TimeRange range) {
    super(season, range);
  }

  public SignupStage(int id, Season season, TimeRange range, Long discordEventId) {
    super(id, season, range, discordEventId);
  }

  public static SignupStage get(Object[] objects) {
    return new SignupStage(
        (int) objects[0],
        new Query<Season>().entity(objects[2]),
        new TimeRange((LocalDateTime) objects[3], (LocalDateTime) objects[4]),
        (Long) objects[5]
    );
  }

  @Override
  public SignupStage create() {
    return new Query<SignupStage>().key("season", season).key("department", "Anmeldung")
        .col("stage_start", range.getStartTime()).col("stage_end", range.getEndTime()).col("discord_event", discordEventId)
        .insert(this);
  }
}
