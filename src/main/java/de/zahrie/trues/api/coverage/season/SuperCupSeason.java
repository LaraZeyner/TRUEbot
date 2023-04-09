package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.stage.Betable;
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
@DiscriminatorValue("super_cup")
public class SuperCupSeason extends PRMSeason implements Betable, Serializable {
  @Serial
  private static final long serialVersionUID = 3498814029985658723L;
}
