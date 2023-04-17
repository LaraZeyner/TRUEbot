package de.zahrie.trues.api.coverage.match.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MatchFormat {
  NO_GAMES(0),
  ONE_GAME(43),
  TWO_GAMES(90),
  BEST_OF_THREE(138),
  FOUR_GAMES(185),
  BEST_OF_FIVE(233);
  private final int duration;
}
