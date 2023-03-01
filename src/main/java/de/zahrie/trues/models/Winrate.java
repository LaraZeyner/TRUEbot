package de.zahrie.trues.models;

import de.zahrie.trues.util.util.TrueNumber;

/**
 * Created by Lara on 27.02.2023 for TRUEbot
 */
public record Winrate(TrueNumber rate) {

  public double getRate() {
    return rate.doubleValue();
  }

  @Override
  public String toString() {
    return rate.percentValue();
  }
}
