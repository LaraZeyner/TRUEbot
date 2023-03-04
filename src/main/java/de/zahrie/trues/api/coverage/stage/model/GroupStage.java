package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.RepeatType;
import de.zahrie.trues.api.coverage.playday.config.DivisionRange;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.playday.config.RelativeTimeRange;
import de.zahrie.trues.api.coverage.playday.config.TimeRepeater;
import de.zahrie.trues.api.coverage.playday.scheduler.SchedulingOption;
import de.zahrie.trues.api.coverage.stage.Scheduleable;
import de.zahrie.trues.api.coverage.stage.StageType;
import de.zahrie.trues.api.datatypes.calendar.Clock;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.calendar.TimeOffset;
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
  public StageType type() {
    return StageType.Gruppenphase;
  }

  @Override
  public int pageId() {
    return 509;
  }


  @Override
  public boolean scheduleable() {
    return true;
  }

  public PlaydayConfig playdayConfig() {
    final var fullWeek = new RelativeTimeRange(Time.MONDAY, new Clock(), 13, new Clock(23, 59));
    final var starter = new RelativeTimeRange(Time.TUESDAY, new Clock(), 13, new Clock(23, 59));
    final TimeOffset upperMatchTime = new TimeOffset(Time.SUNDAY, new Clock(17, 0));
    final TimeOffset lowerMatchTime = new TimeOffset(Time.SUNDAY, new Clock(17, 0));
    return PlaydayConfig.builder()
        .stageType(StageType.Gruppenphase)
        .format(MatchFormat.TWO_GAMES)
        .dayRange(new RelativeTimeRange(Time.MONDAY, new Clock(), Time.SUNDAY, new Clock(23, 59)))
        .options(List.of(
            SchedulingOption.builder()
                .divisionRange(new DivisionRange(LeagueTier.Division_3, LeagueTier.Division_5))
                .defaultTime(upperMatchTime)
                .range(fullWeek)
                .build(),
            SchedulingOption.builder()
                .divisionRange(new DivisionRange(LeagueTier.Division_6, LeagueTier.Division_8))
                .defaultTime(lowerMatchTime)
                .range(fullWeek)
                .build(),
            SchedulingOption.builder()
                .divisionRange(new DivisionRange(LeagueTier.Swiss_Starter, LeagueTier.Swiss_Starter))
                .defaultTime(lowerMatchTime)
                .range(starter)
                .build()
        ))
        .repeater(new TimeRepeater(8, RepeatType.WEEKLY))
        .build();
  }

}
