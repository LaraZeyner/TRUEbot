package de.zahrie.trues.util;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 04.04.2022 for web
 */
@RequiredArgsConstructor
@ToString
public class Const {

  public static class Gamesports {
    public static final int LOWEST_DIVISION = 8;
    public static final String STARTER_NAME = "Swiss Starter";
    public static final int ALTERNATIVE_DIVISION_BREAK = 6;
    public static final int ALTERNATIVE_HOUR_UPPER = 17;
    public static final int ALTERNATIVE_HOUR_LOWER = 15;
  }

  public static final int DISCORD_MESSAGE_MAX_CHARACTERS = 2000;
  public static final int LOG_LEVEL = 500;
  public static final int PLAYER_MMR_DEFAULT_VALUE = 1200; // Silver I 0 LP
  public static final String TIMEOUT_MESSAGE = "SLEEPY TIME";

  public static boolean check() {
    return true;
  }
}
