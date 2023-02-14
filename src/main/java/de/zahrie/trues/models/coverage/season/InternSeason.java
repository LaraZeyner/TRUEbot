package de.zahrie.trues.models.coverage.season;

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
@DiscriminatorValue("intern")
public class InternSeason extends Season implements Serializable {
  @Serial
  private static final long serialVersionUID = 3498814029985658723L;


}
