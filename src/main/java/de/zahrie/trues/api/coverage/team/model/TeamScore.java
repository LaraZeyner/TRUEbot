package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.models.Standing;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter
class TeamScore implements Serializable, Comparable<TeamScore> {
  @Serial
  private static final long serialVersionUID = -4937687342237956160L;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "division")
  @ToString.Exclude
  private League league;

  @Column(name = "current_place")
  private short place;

  @Column(name = "current_wins")
  private short wins;

  @Column(name = "current_losses")
  private short losses;

  public Standing getStanding() {
    return new Standing(wins, losses);
  }

  TeamDestination getDestination() {
    if (this.place < 3) {
      return TeamDestination.PROMOTION;
    }
    if (this.place > 6) {
      return TeamDestination.DEMOTION;
    }
    return TeamDestination.STAY;
  }

  @Override
  public String toString() {
    return this.place + ". (" + this.getWins() + "/" + this.getLosses() + ")";
  }

  private long compareScore() {
    return (10 - league.getTier() * 100_000L) + (1000 - this.place) * 100 + Math.round(getStanding().getWinrate().getRate() * 100);
  }

  @Override
  public int compareTo(TeamScore o) {
    return (int) (compareScore() - o.compareScore());
  }
}
