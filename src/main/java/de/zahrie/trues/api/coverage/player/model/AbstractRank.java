package de.zahrie.trues.api.coverage.player.model;

import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.database.connector.Listing;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

@Log
public record AbstractRank(RankTier tier, Division division, byte points) implements Comparable<AbstractRank> {
  AbstractRank(Tier tier, Division division, byte points) {
    this(RankTier.valueOf(tier.name()), division, points);
  }

  public static AbstractRank fromMMR(int mmr) {
    RankTier rankTier = mmr / 500 > 9 ? RankTier.CHALLENGER : RankTier.values()[mmr / 500];
    final Division division;
    final byte points;
    if (rankTier.ordinal() < RankTier.MASTER.ordinal() && mmr % 500 > 400) {
      if (mmr % 500 < 450) {
        division = Division.I;
        points = 100;
      } else {
        division = Division.IV;
        rankTier = RankTier.values()[rankTier.ordinal() + 1];
        points = 0;
      }
    } else {
      division = rankTier.ordinal() >= RankTier.MASTER.ordinal() || mmr % 500 == 400 ? Division.I : Division.values()[3 - ((mmr / 100) % 5)];
      points = (byte) (rankTier.ordinal() >= RankTier.MASTER.ordinal() ? mmr - (RankTier.MASTER.ordinal() * 500) : (mmr % 100));
    }
    return new AbstractRank(rankTier, division, points);
  }



  @Override
  public String toString() {
    if (tier.equals(RankTier.UNRANKED)) return "Unranked";
    final String divisionString = tier.ordinal() < RankTier.MASTER.ordinal() ? " " + division : "";
    return tier + divisionString + " - " + points + " LP";
  }

  public int getMMR() {
    return (tier.ordinal() >= RankTier.MASTER.ordinal() ? RankTier.MASTER.ordinal() * 500 : tier.ordinal() * 500 + 300 - division.getPoints()) + points;
  }

  @Override
  public int compareTo(@NotNull AbstractRank o) {
    return Integer.compare(o.getMMR(), getMMR());
  }

  @Listing(Listing.ListingType.CAPITALIZE)
  public enum RankTier implements Comparable<RankTier> {
    UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER
  }
}
