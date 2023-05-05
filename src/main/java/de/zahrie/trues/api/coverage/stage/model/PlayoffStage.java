package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.season.OrgaCupSeason;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.Scheduleable;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTimeRange;
import de.zahrie.trues.util.Util;

@Table(value = "coverage_stage", department = "Playoffs")
public class PlayoffStage extends Stage implements Entity<PlayoffStage>, PlayStage, Scheduleable {
  @Serial
  private static final long serialVersionUID = -381890929196558760L;

  public PlayoffStage(Season season, TimeRange range) {
    super(season, range);
  }

  private PlayoffStage(int id, Season season, TimeRange range, Long discordEventId) {
    super(id, season, range, discordEventId);
  }

  @Override
  public Integer pageId() {
    return Util.avoidNull(StageType.fromClass(getClass()), null, StageType::getPrmId);
  }

  @Override
  public boolean isScheduleable() {
    return getSeason() instanceof OrgaCupSeason;
  }

  @Override
  public PlaydayConfig playdayConfig() {
    return PlaydayConfig.builder()
        .stageType(StageType.PLAYOFF_STAGE)
        .format(MatchFormat.BEST_OF_THREE)
        .customDays(List.of(
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(14, 0), 139),
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(18, 0), 139),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(14, 0), 139),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(18, 0), 139),
            new WeekdayTimeRange(DayOfWeek.MONDAY, LocalTime.of(20, 0), 139)
        )).build();
  }

  public static PlayoffStage get(Object[] objects) {
    return new PlayoffStage(
        (int) objects[0],
        new Query<Season>().entity(objects[2]),
        new TimeRange((LocalDateTime) objects[3], (LocalDateTime) objects[4]),
        (Long) objects[5]
    );
  }

  @Override
  public PlayoffStage create() {
    return new Query<PlayoffStage>().key("season", season).key("department", "PlayoffStage")
        .col("stage_start", range.getStartTime()).col("stage_end", range.getEndTime()).col("discord_event", discordEventId)
        .insert(this);
  }

}
