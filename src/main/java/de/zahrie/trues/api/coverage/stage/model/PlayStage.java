package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.coverage.stage.IdAble;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
public class PlayStage extends Stage implements Betable, IdAble, Playable, Serializable {
  @Serial
  private static final long serialVersionUID = 7394534903088339480L;


  @OneToMany(mappedBy = "stage")
  @ToString.Exclude
  private Set<League> leagues = new LinkedHashSet<>();

  @OneToMany(mappedBy = "stage")
  @ToString.Exclude
  private Set<Playday> playdays = new LinkedHashSet<>();

  @Override
  public int pageId() {
    return 0;
  }

  @Override
  public PlaydayConfig playdayConfig() {
    return null;
  }
}
