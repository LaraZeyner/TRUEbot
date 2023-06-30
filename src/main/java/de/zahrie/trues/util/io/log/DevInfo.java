package de.zahrie.trues.util.io.log;

import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.StringUtils;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@NoArgsConstructor
public class DevInfo extends AbstractLog<DevInfo> {
  private static final TextChannel LOGGING_CHANNEL = Nunu.getInstance().getClient().getTextChannelById(Const.Channels.DEV_LOGGING_CHANNEL);

  public DevInfo(String message) {
    super(message);
  }

  public DevInfo doCommand(DiscordUser user, String command, String full) {
    this.level = Level.COMMAND;
    this.message = user.getNickname() + " -> " + command + full;
    if (LOGGING_CHANNEL == null) throw new NullPointerException("Dev-Log Channel wurde gelöscht");
    LOGGING_CHANNEL.sendMessage(StringUtils.keep(toString(), 2000)).queue();
    return this;
  }

  @Override
  protected DevInfo doLog() {
    if (Level.DISCORD_LOG.contains(level)) {
      if (LOGGING_CHANNEL == null) throw new NullPointerException("Dev-Log Channel wurde gelöscht");
      LOGGING_CHANNEL.sendMessage(StringUtils.keep(toString(), 2000)).queue();
    }
    return this;
  }
}
