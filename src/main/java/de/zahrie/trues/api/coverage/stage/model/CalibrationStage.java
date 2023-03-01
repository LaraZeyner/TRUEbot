package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.MatchFormat;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.playday.config.RelativeTimeRange;
import de.zahrie.trues.api.coverage.stage.StageType;
import de.zahrie.trues.util.util.Clock;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("Kalibrierungsphase")
public class CalibrationStage extends PlayStage implements Serializable {
  @Serial
  private static final long serialVersionUID = -8942194266655689760L;

  @Override
  public StageType type() {
    return StageType.Kalibrierungsphase;
  }

  @Override
  public int pageId() {
    return 506;
  }

  @Override
  public PlaydayConfig playdayConfig() {
    return PlaydayConfig.builder()
        .stageType(type())
        .format(MatchFormat.ONE_GAME)
        .customDays(List.of(
            new RelativeTimeRange(Calendar.SATURDAY, new Clock(14, 0), 50),
            new RelativeTimeRange(Calendar.SATURDAY, new Clock(15, 15), 50),
            new RelativeTimeRange(Calendar.SATURDAY, new Clock(16, 30), 50),
            new RelativeTimeRange(Calendar.SATURDAY, new Clock(17, 45), 50),
            new RelativeTimeRange(Calendar.SUNDAY, new Clock(14, 0), 50),
            new RelativeTimeRange(Calendar.SUNDAY, new Clock(15, 15), 50),
            new RelativeTimeRange(Calendar.SUNDAY, new Clock(16, 30), 50),
            new RelativeTimeRange(Calendar.SUNDAY, new Clock(17, 45), 50)
        )).build();
  }
}
