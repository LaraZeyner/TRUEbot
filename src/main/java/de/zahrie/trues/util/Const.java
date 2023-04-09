package de.zahrie.trues.util;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class Const {

  public static class Gamesports {
    public static final String STARTER_NAME = "Swiss Starter";
  }

  public static final int DISCORD_MESSAGE_MAX_CHARACTERS = 2000;
  public static final int LOG_LEVEL = 500;
  public static final String TIMEOUT_MESSAGE = "SLEEPY TIME";
  public static final String THREAD_CHANNEL_START = "Scouting vs. ";
  public static final int PLAYER_MMR_DEFAULT_VALUE = 1200; // Silver I 0 LP

  public static final double PREDICTION_FACTOR = .03;

  public static boolean check() {
    return true;
  }
}
