package de.zahrie.trues.models;

import de.zahrie.trues.api.datatypes.number.TrueNumber;

public record Winrate(TrueNumber rate) {
  @Override
  public String toString() {
    return rate.percentValue();
  }

}
