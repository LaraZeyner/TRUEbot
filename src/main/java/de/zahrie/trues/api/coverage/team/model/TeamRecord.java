package de.zahrie.trues.api.coverage.team.model;

import de.zahrie.trues.util.Format;

public record TeamRecord(short seasons, short wins, short losses) {
  public Standing getStanding() {
    return new Standing(wins, losses);
  }

  @Override
  public String toString() {
    return getStanding().format(Format.SHORT) + " - " + seasons + " Seasons";
  }
}
