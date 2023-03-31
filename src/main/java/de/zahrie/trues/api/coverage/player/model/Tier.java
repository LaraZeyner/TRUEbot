package de.zahrie.trues.api.coverage.player.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Tier {
  BRONZE(2),
  CHALLENGER(9),
  DIAMOND(6),
  GOLD(4),
  GRANDMASTER(8),
  IRON(1),
  MASTER(7),
  PLATINUM(5),
  SILVER(3),
  UNRANKED(0);

  private final int level;
  
  public static Tier fromTier(com.merakianalytics.orianna.types.common.Tier tier) {
    return Tier.valueOf(tier.name());
  }
}
