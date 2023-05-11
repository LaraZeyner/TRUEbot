package de.zahrie.trues.util.io.log;

import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Console extends AbstractLog<Console> {
  public Console(String message) {
    super(message);
  }

  public Console doCommand(DiscordUser user, String command, String full) {
    return null;
    // do nothing
  }

  @Override
  protected Console doLog() {
    if (Level.CONSOLE_LOG.contains(level)) {
      if (level.ordinal() >= Level.ERROR.ordinal()) System.err.println(this);
      else System.out.println(this);
    }
    return this;
  }
}
