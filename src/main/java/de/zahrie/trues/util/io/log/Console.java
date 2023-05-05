package de.zahrie.trues.util.io.log;

import de.zahrie.trues.api.discord.user.DiscordUser;

public class Console extends Log {
  public void doCommand(DiscordUser user, String command, String full) {
    // do nothing
  }

  @Override
  protected void doLog(Level level, String msg) {
    if (Level.CONSOLE_LOG.contains(level)) {
      if (level.ordinal() >= Level.ERROR.ordinal()) System.err.println(getMessage(level, msg));
      else System.out.println(getMessage(level, msg));
    }
  }
}
