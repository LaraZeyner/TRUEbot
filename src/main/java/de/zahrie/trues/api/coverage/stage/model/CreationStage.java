package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;

@Table(value = "coverage_stage", department = "Auslosung")
public class CreationStage extends Stage implements Entity<CreationStage>, WaitingStage {
  @Serial
  private static final long serialVersionUID = 4107910991901449582L;

  public CreationStage(Season season, TimeRange range) {
    super(season, range);
  }

  public CreationStage(int id, Season season, TimeRange range, Long discordEventId) {
    super(id, season, range, discordEventId);
  }

  public static CreationStage get(Object[] objects) {
    return new CreationStage(
        (int) objects[0],
        new Query<Season>().entity(objects[2]),
        new TimeRange((LocalDateTime) objects[3], (LocalDateTime) objects[4]),
        (Long) objects[5]
    );
  }

  @Override
  public CreationStage create() {
    return new Query<CreationStage>().key("season", season).key("department", "Auslosung")
        .col("stage_start", range.getStartTime()).col("stage_end", range.getEndTime()).col("discord_event", discordEventId)
        .insert(this);
  }
}
