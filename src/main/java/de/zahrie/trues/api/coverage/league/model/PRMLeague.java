package de.zahrie.trues.api.coverage.league.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.scheduler.PlaydayScheduler;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.io.request.URLType;
import jakarta.persistence.Column;
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
@DiscriminatorValue("not null")
public class PRMLeague extends League implements Serializable {

  @Serial
  private static final long serialVersionUID = -4755609416246322480L;

  public static PRMLeague build(String divisionName, PlayStage stage, int prmId) {
    final var league = new PRMLeague(prmId);
    league.setStage(stage);
    league.setName(divisionName);
    Database.insert(league);
    return league;
  }

  @Column(name = "prm_id", nullable = false)
  private int prmId;

  public LocalDateTime getAlternative(Playday playday) {
    final PlaydayScheduler scheduler = PlaydayScheduler.create(getStage(), playday.getId(), getTier());
    return scheduler.defaultTime();
  }

  public LeagueTier getTier() {
    return LeagueTier.fromName(getName());
  }

  public boolean isStarter() {
    return getName().equals(Const.Gamesports.STARTER_NAME) || getName().contains(Const.Gamesports.CALIBRATION_NAME) || getName().contains(Const.Gamesports.PLAYOFF_NAME);
  }

  public String getUrl() {
    return String.format(URLType.LEAGUE.getUrlName(), ((PRMSeason) getStage().getSeason()).getPrmId(), getStage().pageId(), prmId);
  }
}
