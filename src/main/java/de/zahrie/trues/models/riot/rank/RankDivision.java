package de.zahrie.trues.models.riot.rank;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RankDivision {
  private RankTier tier = RankTier.Unranked;
  private Integer division = null;

  int mmr() {
    return this.tier.mmr + (4 - this.division) * 100;
  }

  @Override
  public String toString() {
    String suffix;
    if (this.division == null) {
      suffix = "";
    } else if (this.division == 4) {
      suffix = "IV";
    } else {
      suffix = "I".repeat(this.division);
    }
    return this.tier.name() + " " + suffix;
  }
}
