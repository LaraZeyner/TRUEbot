package de.zahrie.trues.discord.command.models;

import java.util.List;

import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.discord.channel.AbstractDiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.logging.MessageLog;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "chatlog", descripion = "Chatlog dieses Channels einsehen", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class ChatlogCommand extends SlashCommand {
  @Override
  public boolean execute(SlashCommandInteractionEvent event) {
    final AbstractDiscordChannel channel = DiscordChannelFactory.getDiscordChannel((GuildChannel) event.getChannel());
    final List<MessageLog> logList = new Query<>(MessageLog.class).where("discord_channel", channel).descending("log_time").entityList();
    final EmbedBuilder builder = new EmbedBuilder().setTitle("Chatlog von " + event.getChannel().getName());
    if (logList.isEmpty()) {
      builder.addField("keine Einträge", "In diesem Channel wurden noch keine Nachrichten gelöscht.", false);
    } else {
      for (int i = 0; i < logList.size(); i++) {
        if (i == 25) break;
        final MessageLog messageLog = logList.get(i);
        builder.addField(
            TimeFormat.DISCORD.of(messageLog.getTimestamp()) + " von " + messageLog.getTarget().getMention() + " (" + messageLog.getReason() + ")",
            messageLog.getDetails(),
            false
        );
      }
    }

    event.getHook().sendMessageEmbeds(builder.build()).queue();
    return true;
  }
}
