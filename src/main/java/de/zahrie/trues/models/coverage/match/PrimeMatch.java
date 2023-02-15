package de.zahrie.trues.models.coverage.match;

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
@DiscriminatorValue("2")
@NamedQuery(name = "PrimeMatch.fromMatchId", query = "FROM PrimeMatch WHERE matchId = :matchId")
public class PrimeMatch extends ScheduleableMatch implements Serializable {
  @Serial
  private static final long serialVersionUID = -6145053153275706756L;

  @Column(name = "match_id")
  private Integer matchId;

}
