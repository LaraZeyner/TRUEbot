package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter
public class TeamRecord implements Serializable {
  @Serial
  private static final long serialVersionUID = 8881940634396296302L;

  @Column(name = "seasons", columnDefinition = "TINYINT UNSIGNED")
  private Short seasons;

  @Column(name = "total_wins")
  private Short wins;

  @Column(name = "total_losses")
  private Short losses;

  public Standing getStanding() {
    // TODO (Abgie) 15.03.2023: never used
    return new Standing(wins, losses);
  }

}
