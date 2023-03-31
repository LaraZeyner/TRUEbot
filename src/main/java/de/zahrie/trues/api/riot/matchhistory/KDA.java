package de.zahrie.trues.api.riot.matchhistory;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Lara on 31.03.2023 for TRUEbot
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KDA {
  @Column(name = "kills", columnDefinition = "SMALLINT UNSIGNED")
  private short kills;

  @Column(name = "deaths", columnDefinition = "SMALLINT UNSIGNED")
  private short deaths;

  @Column(name = "assists", columnDefinition = "SMALLINT UNSIGNED")
  private short assists;

  @Override
  public String toString() {
    return kills + "/" + deaths + "/" + assists;
  }
}
