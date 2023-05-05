package de.zahrie.trues.util.io.log;

import java.time.LocalDateTime;

import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.logging.CommandLog;
import de.zahrie.trues.api.logging.CustomLog;

public class SQLInfo extends Log {

  public void doCommand(DiscordUser user, String command, String full) {
    new CommandLog(LocalDateTime.now(), user, command, full);
  }

  @Override
  protected void doLog(Level level, String msg) {
    if (Level.DATABASE_LOG.contains(level)) new CustomLog(LocalDateTime.now(), msg, level).create();
  }
}
