package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.datatypes.calendar.WeekdayTimeRange;
import de.zahrie.trues.util.Util;
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
@DiscriminatorValue("Kalibrierungsphase")
public class CalibrationStage extends PlayStage implements Serializable {
  @Serial
  private static final long serialVersionUID = -8942194266655689760L;

  @Override
  public Integer pageId() {
    return Util.avoidNull(StageType.fromClass(getClass()), null, StageType::getPrmId);
  }

  @Override
  public PlaydayConfig playdayConfig() {
    return PlaydayConfig.builder()
        .stageType(StageType.CALIBRATION_STAGE)
        .format(MatchFormat.ONE_GAME)
        .customDays(List.of(
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(14, 0), 50),
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(15, 15), 50),
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(16, 30), 50),
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(17, 45), 50),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(14, 0), 50),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(15, 15), 50),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(16, 30), 50),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(17, 45), 50)
        )).build();
  }
}
