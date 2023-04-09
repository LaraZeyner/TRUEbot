package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.PlaydayFactory;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
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

  public Scrimmage(Playday matchday, LocalDateTime start) {
    super(matchday, start);
  }

  public Scrimmage(LocalDateTime start) {
    this(PlaydayFactory.current(), start);
  }

  public String display() {
    return getId() + " | " + TimeFormat.DEFAULT.of(getStart()) + " | " + getHomeName() + " vs. " + getGuestName();
  }
}
