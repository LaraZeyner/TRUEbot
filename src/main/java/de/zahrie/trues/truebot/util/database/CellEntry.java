package de.zahrie.trues.truebot.util.database;

import de.zahrie.trues.truebot.dc.util.cmd.annotations.Column;

/**
 * Created by Lara on 13.02.2023 for TRUEbot
 */
public record CellEntry(Object entry) {

  @Override
  public String toString() {
    return String.valueOf(this.entry);
  }

  public String round() {
    return this.round(0);
  }

  public String round(Column column) {
    if (this.entry instanceof Double || this.entry instanceof Float) {
      final double d = (double) this.entry;
      if (d != 0) {
        return String.valueOf(Math.round(d * Math.pow(10, column.round())) / Math.pow(10, column.round()));
      }
    }
    return this.toString();
  }

  public String round(int amount) {
    if (this.entry instanceof Double || this.entry instanceof Float) {
      final double d = (double) this.entry;
      if (d != 0) {
        return String.valueOf(Math.round(d * Math.pow(10, amount)) / Math.pow(10, amount));
      }
    }
    return this.toString();
  }

}
