package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.RepeatType;
import de.zahrie.trues.api.coverage.playday.config.DivisionRange;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.playday.config.TimeRepeater;
import de.zahrie.trues.api.coverage.playday.scheduler.SchedulingOption;
import de.zahrie.trues.api.coverage.stage.Scheduleable;
import de.zahrie.trues.api.coverage.stage.StageType;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTime;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTimeRange;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("Gruppenphase")
public class GroupStage extends PlayStage implements Scheduleable, Serializable {
  @Serial
  private static final long serialVersionUID = -8729614783748936462L;

  @Override
  public int pageId() {
    return 509;
  }


  @Override
  public boolean scheduleable() {
    return true;
  }

  public PlaydayConfig playdayConfig() {
    return PlaydayConfig.builder()
        .stageType(StageType.GROUPS)
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

}
