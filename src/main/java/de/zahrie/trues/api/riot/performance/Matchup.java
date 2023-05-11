package de.zahrie.trues.api.riot.performance;

import de.zahrie.trues.api.riot.champion.Champion;

public record Matchup(Champion champion, Champion opponent) {
  @Override
  public String toString() {
    return champion.getName() + " vs " + (opponent == null ? "no data" : opponent.getName());
  }
}
