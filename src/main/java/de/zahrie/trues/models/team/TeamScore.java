package de.zahrie.trues.models.team;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
class TeamScore extends TeamRecord implements Serializable, Comparable<TeamScore> {
  private int place;

  public TeamScore(int place, int wins, int losses) {
    super(wins, losses);
    this.place = place;
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

  @Override
  public int compareTo(TeamScore o) {
    return (this.place + Math.round(this.getWinrate() * 100) / 100) -
           (o.place + Math.round(o.getWinrate() * 100) / 100);
  }
}
