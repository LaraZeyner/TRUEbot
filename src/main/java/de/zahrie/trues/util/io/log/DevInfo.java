package de.zahrie.trues.util.io.log;

import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Const;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DevInfo extends Log{
  private static final TextChannel LOGGING_CHANNEL = Nunu.getInstance().getClient().getTextChannelById(Const.LOGGING_CHANNEL);

  public void doCommand(DiscordUser user, String command, String full) {
    LOGGING_CHANNEL.sendMessage(getMessage(Level.COMMAND, user.getMention() + " -> " + command + full)).queue();
  }

  @Override
  protected void doLog(Level level, String msg) {
    if (Level.DISCORD_LOG.contains(level)) LOGGING_CHANNEL.sendMessage(getMessage(level, msg)).queue();
  }
}
