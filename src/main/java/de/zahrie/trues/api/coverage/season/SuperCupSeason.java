package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.stage.Betable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("super_cup")
@NamedQuery(name = "PrimeSeason.fromSeasonId", query = "FROM PrimeSeason WHERE prmId = :seasonId")
@NamedQuery(name = "PrimeSeason.fromName", query = "FROM PrimeSeason WHERE fullName = :name")
public class SuperCupSeason extends PrimeSeason implements Betable, Serializable {
  @Serial
  private static final long serialVersionUID = 3498814029985658723L;

  @Override
  public CoverageDepartment type() {
    return CoverageDepartment.Super_Cup;
  }
}
