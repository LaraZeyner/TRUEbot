package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@DiscriminatorValue("bet")
public class TournamentMatch extends Match implements Serializable {
  @Serial
  private static final long serialVersionUID = 6103645801571452429L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coverage_group")
  @ToString.Exclude
  private League league;

  public TournamentMatch(Playday matchday, Calendar start, League league) {
    super(matchday, start);
    this.league = league;
  }
}
