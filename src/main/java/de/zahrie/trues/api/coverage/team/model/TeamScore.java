package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import de.zahrie.trues.util.Format;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter
public class TeamScore implements Serializable, Comparable<TeamScore> {
  @Serial
  private static final long serialVersionUID = -4937687342237956160L;

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
    return Comparator.comparing(TeamScore::getPlace)
        .thenComparing((TeamScore o1) -> o1.getStanding().getWinrate().rate(), Comparator.reverseOrder())
        .compare(this, o);
  }
}
