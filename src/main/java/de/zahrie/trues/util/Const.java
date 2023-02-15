package de.zahrie.trues.util;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 04.04.2022 for web
 */
@RequiredArgsConstructor
@ToString
public class Const {
  public static final int DISCORD_MESSAGE_MAX_CHARACTERS = 2000;
  public static final int LOG_LEVEL = 500;
  public static final int PLAYER_MMR_DEFAULT_VALUE = 1200; // Silver I 0 LP
  public static final String TIMEOUT_MESSAGE = "SLEEPY TIME";

  public static boolean check() {
    return true;
  }
}
