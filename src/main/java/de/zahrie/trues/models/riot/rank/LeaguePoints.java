package de.zahrie.trues.models.riot.rank;

import java.io.Serializable;

public record LeaguePoints(int points) implements Serializable {
  @Override
  public String toString() {
    return this.points + " LP";
  }
}
