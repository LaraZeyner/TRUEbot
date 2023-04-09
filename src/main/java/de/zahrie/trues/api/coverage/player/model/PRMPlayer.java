package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.io.Serializable;

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
public class PRMPlayer extends Player implements Serializable {
  @Serial
  private static final long serialVersionUID = 8712841145904125387L;

  @Column(name = "prm_id", unique = true)
  private Integer prmUserId;

  public PRMPlayer(String summonerName, String puuid, Integer prmUserId) {
    super(summonerName, puuid);
    this.prmUserId = prmUserId;
  }

  @Override
  public boolean equals(Object other) {
    return super.equals(other) || (other instanceof PRMPlayer && this.prmUserId.equals(((PRMPlayer) other).prmUserId));
  }
}
