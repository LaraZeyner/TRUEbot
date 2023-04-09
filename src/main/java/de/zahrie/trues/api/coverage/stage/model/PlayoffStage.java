package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.season.OrgaCupSeason;
import de.zahrie.trues.api.coverage.stage.Scheduleable;
import de.zahrie.trues.api.coverage.stage.StageType;
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
@DiscriminatorValue("Playoffs")
public class PlayoffStage extends PlayStage implements Scheduleable, Serializable {
  @Serial
  private static final long serialVersionUID = -5748214049845502578L;

  @Override
  public int pageId() {
    return 512;
  }

  @Override
  public boolean scheduleable() {
    return getSeason() instanceof OrgaCupSeason;
  }

  @Override
  public PlaydayConfig playdayConfig() {
    return PlaydayConfig.builder()
        .stageType(StageType.PLAYOFFS)
        .format(MatchFormat.BEST_OF_THREE)
        .customDays(List.of(
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(14, 0), 139),
            new WeekdayTimeRange(DayOfWeek.SATURDAY, LocalTime.of(18, 0), 139),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(14, 0), 139),
            new WeekdayTimeRange(DayOfWeek.SUNDAY, LocalTime.of(18, 0), 139),
            new WeekdayTimeRange(DayOfWeek.MONDAY, LocalTime.of(20, 0), 139)
        )).build();
  }

}
