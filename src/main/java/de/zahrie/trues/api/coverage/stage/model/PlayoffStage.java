package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.playday.config.RelativeTimeRange;
import de.zahrie.trues.api.coverage.season.CoverageDepartment;
import de.zahrie.trues.api.coverage.stage.Scheduleable;
import de.zahrie.trues.api.coverage.stage.StageType;
import de.zahrie.trues.api.datatypes.calendar.Clock;
import de.zahrie.trues.api.datatypes.calendar.Time;
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
  public StageType type() {
    return StageType.Playoffs;
  }

  @Override
  public int pageId() {
    return 512;
  }

  @Override
  public boolean scheduleable() {
    return getSeason().getCoverageDepartment().equals(CoverageDepartment.Orga_Cup);
  }

  @Override
  public PlaydayConfig playdayConfig() {
    final Clock earlyTime = new Clock(14, 0);
    final Clock laterTime = new Clock(18, 0);
    final Clock latestTime = new Clock(20, 0);
    return PlaydayConfig.builder()
        .stageType(StageType.Kalibrierungsphase)
        .format(MatchFormat.BEST_OF_THREE)
        .customDays(List.of(
            new RelativeTimeRange(Time.SATURDAY, earlyTime, 139),
            new RelativeTimeRange(Time.SATURDAY, laterTime, 139),
            new RelativeTimeRange(Time.SUNDAY, earlyTime, 139),
            new RelativeTimeRange(Time.SUNDAY, laterTime, 139),
            new RelativeTimeRange(Time.MONDAY, latestTime, 139)
        )).build();
  }

}
