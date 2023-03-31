package de.zahrie.trues.api.coverage.player.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Division {
  I(4),
  II(3),
  III(2),
  IV(1),
  V(0);

  private final int level;

  public static Division fromDivision(com.merakianalytics.orianna.types.common.Division division) {
    return Division.valueOf(division.name());
  }
}
