package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.number.TrueNumber;
import de.zahrie.trues.util.Format;
import org.jetbrains.annotations.NotNull;

public record Standing(int wins, int losses) implements Serializable, Comparable<Standing> {
  @Serial
  private static final long serialVersionUID = -8830455150033695496L;

  public Winrate getWinrate() {
    return new Winrate(new TrueNumber((double) this.wins).divide(this.losses));
  }

  public int getGames() {
    return wins + losses;
  }

  @Override
  public String toString() {
    return wins + " : " + losses;
  }

  public String format(Format format) {
    if (format.equals(Format.LONG)) {
      return wins + " : " + losses;
    }

    if (format.equals(Format.SHORT)) {
      return wins + ":" + losses;
    }

    if (format.equals(Format.ADDITIONAL)) {
      return wins + ":" + losses + " (" + getWinrate() + ")";
    }
    return null;
  }

  @Override
  public int compareTo(@NotNull Standing o) {
    return getWinrate().compareTo(o.getWinrate());
  }

  public record Winrate(TrueNumber rate) implements Comparable<Winrate> {
    @Override
    public String toString() {
      return rate.percentValue();
    }

    @Override
    public int compareTo(@NotNull Winrate o) {
      return rate.compareTo(o.rate);
    }
  }
}
