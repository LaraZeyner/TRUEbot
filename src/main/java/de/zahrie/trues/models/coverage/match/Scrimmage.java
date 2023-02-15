package de.zahrie.trues.models.coverage.match;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("scrimmage")
public class Scrimmage extends Match implements Serializable {
  @Serial
  private static final long serialVersionUID = -5376878014104117438L;

}
