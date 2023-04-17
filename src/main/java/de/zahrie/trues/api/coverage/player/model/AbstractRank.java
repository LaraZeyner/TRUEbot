package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@Log
public class AbstractRank implements Serializable, Comparable<AbstractRank> {
  @Serial
  private static final long serialVersionUID = 7228597517124074472L;

  @Column(name = "tier", length = 11, nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private RankTier tier;

  @Column(name = "division", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private Division division;

  @Column(name = "points", nullable = false)
  private byte points;

  @Override
  public String toString() {
    if (getTier().equals(RankTier.UNRANKED)) return "Unranked";
    final String divisionString = tier.ordinal() < RankTier.MASTER.ordinal() ? " " + division : "";
    return tier + divisionString + " - " + points + " LP";
  }

  AbstractRank(int mmr) {
    this.tier = mmr / 500 > 9 ? RankTier.CHALLENGER : RankTier.values()[mmr / 500];
    if (this.tier.ordinal() < RankTier.MASTER.ordinal() && mmr % 500 > 400) {
      if (mmr % 500 < 450) {
        this.division = Division.I;
        this.points = 100;
      } else {
        this.division = Division.IV;
        this.tier = RankTier.values()[tier.ordinal()+1];
        this.points = 0;
      }
    } else {
      this.division = this.tier.ordinal() >= RankTier.MASTER.ordinal() || mmr % 500 == 400 ? Division.I : Division.values()[3 - ((mmr / 100) % 5)];
      this.points = (byte) (this.tier.ordinal() >= RankTier.MASTER.ordinal() ? mmr - (RankTier.MASTER.ordinal() * 500) : (mmr % 100));
    }
  }

  AbstractRank(Tier tier, Division division, byte points) {
    this.tier = RankTier.valueOf(tier.name());
    this.division = division;
    this.points = points;
  }

  public int getMMR() {
    return (tier.ordinal() >= RankTier.MASTER.ordinal() ? RankTier.MASTER.ordinal() * 500 : tier.ordinal() * 500 + 300 - division.ordinal() * 100) + points;
  }

  @Override
  public int compareTo(@NotNull AbstractRank o) {
    return Comparator.comparing(AbstractRank::getMMR).compare(this, o);
  }

  public enum RankTier implements Comparable<RankTier> {
    UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER
  }
}
