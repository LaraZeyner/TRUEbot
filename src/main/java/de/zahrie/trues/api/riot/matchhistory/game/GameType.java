package de.zahrie.trues.api.riot.matchhistory.game;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GameType {
  TOURNAMENT(0),
  CUSTOM(0),
  CLASH(700),
  RANKED_FLEX(410),
  RANKED_SOLO(400),
  NORMAL_DRAFT(420),
  NORMAL_BLIND(430);

  private final int id;

  public static GameType fromId(int id) {
    return Arrays.stream(GameType.values()).filter(gameType -> gameType.getId() == id).findFirst().orElse(null);
  }
}
