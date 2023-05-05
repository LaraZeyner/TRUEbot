package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.RepeatType;
import de.zahrie.trues.api.coverage.playday.config.DivisionRange;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.playday.config.TimeRepeater;
import de.zahrie.trues.api.coverage.playday.scheduler.SchedulingOption;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.Scheduleable;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTime;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTimeRange;
import de.zahrie.trues.util.Util;

@Table(value = "coverage_stage", department = "Gruppenphase")
public class GroupStage extends Stage implements Entity<GroupStage>, PlayStage, Scheduleable {
  @Serial
  private static final long serialVersionUID = -8729614783748936462L;

  public GroupStage(Season season, TimeRange range) {
    super(season, range);
  }

  private GroupStage(int id, Season season, TimeRange range, Long discordEventId) {
    super(id, season, range, discordEventId);
  }

  @Override
  public Integer pageId() {
    return Util.avoidNull(StageType.fromClass(getClass()), null, StageType::getPrmId);
  }

  public PlaydayConfig playdayConfig() {
    return PlaydayConfig.builder()
        .stageType(StageType.GROUP_STAGE)
        .format(MatchFormat.TWO_GAMES)
        .dayRange(new WeekdayTimeRange(WeekdayTime.min(DayOfWeek.MONDAY), WeekdayTime.max(DayOfWeek.SUNDAY)))
        .options(List.of(
            SchedulingOption.builder()
                .divisionRange(new DivisionRange(LeagueTier.Division_3, LeagueTier.Division_5))
                .defaultTime(new WeekdayTime(DayOfWeek.SUNDAY, LocalTime.of(17, 0)))
                .range(new WeekdayTimeRange(WeekdayTime.min(DayOfWeek.MONDAY), WeekdayTime.max(DayOfWeek.SUNDAY), 1))
                .build(),
            SchedulingOption.builder()
                .divisionRange(new DivisionRange(LeagueTier.Division_6, LeagueTier.Division_8))
                .defaultTime(new WeekdayTime(DayOfWeek.SUNDAY, LocalTime.of(15, 0)))
                .range(new WeekdayTimeRange(WeekdayTime.min(DayOfWeek.MONDAY), WeekdayTime.max(DayOfWeek.SUNDAY), 1))
                .build(),
            SchedulingOption.builder()
                .divisionRange(new DivisionRange(LeagueTier.Swiss_Starter, LeagueTier.Swiss_Starter))
                .defaultTime(new WeekdayTime(DayOfWeek.SUNDAY, LocalTime.of(15, 0)))
                .range(new WeekdayTimeRange(WeekdayTime.min(DayOfWeek.TUESDAY), WeekdayTime.max(DayOfWeek.SUNDAY)))
                .build()
        ))
        .repeater(new TimeRepeater(8, RepeatType.WEEKLY))
        .build();
  }

  public static GroupStage get(Object[] objects) {
    return new GroupStage(
        (int) objects[0],
        new Query<Season>().entity(objects[2]),
        new TimeRange((LocalDateTime) objects[3], (LocalDateTime) objects[4]),
        (Long) objects[5]
    );
  }

  @Override
  public GroupStage create() {
    return new Query<GroupStage>().key("season", season).key("department", "Gruppenphase")
        .col("stage_start", range.getStartTime()).col("stage_end", range.getEndTime()).col("discord_event", discordEventId)
        .insert(this);
  }

  @Override
  public boolean isScheduleable() {
    return true;
  }
}
