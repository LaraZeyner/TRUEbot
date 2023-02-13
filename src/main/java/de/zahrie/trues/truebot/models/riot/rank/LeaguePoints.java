package de.zahrie.trues.truebot.models.riot.rank;

import java.io.Serializable;

public record LeaguePoints(int points) implements Serializable {
  @Override
  public String toString() {
    return this.points + " LP";
  }
}
