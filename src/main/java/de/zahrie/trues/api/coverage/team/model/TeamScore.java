package de.zahrie.trues.api.coverage.team.model;

import java.io.Serializable;
import java.util.Comparator;

import de.zahrie.trues.util.Format;
import org.jetbrains.annotations.NotNull;

public record TeamScore(Short place, Short wins, Short losses) implements Serializable, Comparable<TeamScore> {
  public static TeamScore disqualified() {
    return new TeamScore(null, null, null);
  }

  public Standing getStanding() {
    return new Standing(wins, losses);
  }

  TeamDestination getDestination() {
    if (this.place < 3) return TeamDestination.PROMOTION;
    if (this.place > 6) return TeamDestination.DEMOTION;
    return TeamDestination.STAY;
  }

  @Override
  public String toString() {
    return this.place + ". " + getStanding().format(Format.ADDITIONAL);
  }

  @Override
  public int compareTo(@NotNull TeamScore o) {
    return Comparator.comparing(TeamScore::place)
        .thenComparing((TeamScore o1) -> o1.getStanding().getWinrate().rate(), Comparator.reverseOrder())
        .compare(this, o);
  }

  public boolean isDisqualified() {
    return place == null && wins == null && losses == null;
  }
}
