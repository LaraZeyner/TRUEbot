package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
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
@NamedQuery(name = "PrimePlayer.fromPrimeId", query = "FROM PrimePlayer WHERE prmUserId = :primeId")
@NamedQuery(name = "PrimePlayer.fromPuuid", query = "FROM PrimePlayer WHERE puuid = :puuid")
@NamedQuery(name = "PrimePlayer.fromName", query = "FROM PrimePlayer WHERE summonerName = :name")
public class PrimePlayer extends Player implements Serializable {
  @Serial
  private static final long serialVersionUID = 8712841145904125387L;

  @Column(name = "prm_id")
  private Integer prmUserId;

  public PrimePlayer(String summonerName, String puuid, Integer prmUserId) {
    super(summonerName, puuid);
    this.prmUserId = prmUserId;
  }

  @Override
  public boolean equals(Object other) {
    return super.equals(other) || (other instanceof PrimePlayer && this.prmUserId.equals(((PrimePlayer) other).prmUserId));
  }
}
