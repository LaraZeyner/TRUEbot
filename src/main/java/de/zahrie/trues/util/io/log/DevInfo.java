package de.zahrie.trues.util.io.log;

import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.StringUtils;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@NoArgsConstructor
public class DevInfo extends AbstractLog<DevInfo> {
  private static final TextChannel LOGGING_CHANNEL = Nunu.getInstance().getClient().getTextChannelById(Const.LOGGING_CHANNEL);

  public DevInfo(String message) {
    super(message);
  }

  public DevInfo doCommand(DiscordUser user, String command, String full) {
    this.level = Level.COMMAND;
    this.message = user.getMention() + " -> " + command + full;
    LOGGING_CHANNEL.sendMessage(StringUtils.keep(toString(), 2000)).queue();
    return this;
  }

  @Override
  protected DevInfo doLog() {
    if (Level.DISCORD_LOG.contains(level)) LOGGING_CHANNEL.sendMessage(StringUtils.keep(toString(), 2000)).queue();
    return this;
  }
}
