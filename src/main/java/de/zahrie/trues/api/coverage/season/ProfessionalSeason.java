package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.io.Serializable;

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
@DiscriminatorValue("pro")
public class ProfessionalSeason extends Season implements Serializable {
  @Serial
  private static final long serialVersionUID = 3498814029985658723L;

  @Override
  public CoverageDepartment type() {
    return CoverageDepartment.Professional;
  }
}
