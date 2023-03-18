
package de.zahrie.trues.discord.command.models.channel;

import java.util.Map;

import de.zahrie.trues.api.discord.channel.ChannelRolePattern;
import de.zahrie.trues.api.discord.channel.ChannelType;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.Nunu;
import de.zahrie.trues.util.util.Util;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "create", descripion = "Channel erstellen", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "art", description = "Art des Channels", choices = {"Forum", "News", "Stage", "Text", "Voice"}),
    @Option(name = "name", description = "Name des Channels", required = false),
    @Option(name = "typ", description = "Typ des Channels", required = false, choices = {"Socials", "Event", "Leaderboard", "Orga Intern", "Staff Intern", "Content Intern"})
})
public class ChannelCreateCommand extends SlashCommand {
  @Override
  @Msg("Der Auftrag wurde erfolgreich durchgeführt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final ChannelType type = get("typ", ChannelType.class);
    final String name = get("name").getAsString();
    final ChannelKind kind = get("art", ChannelKind.class);
    if (event.getChannel() instanceof ICategorizableChannel channel) {
      if (getInvokingMember().hasPermission(Util.nonNull(channel.getParentCategory()), Permission.MANAGE_CHANNEL)) {
        kind.createChannel(name, channel, type);
        return sendMessage();
      }
    }
    return errorMessage();
  }

  public enum ChannelKind {
    FORUM,
    NEWS,
    STAGE,
    TEXT,
    VOICE;

    public void createChannel(String name, ICategorizableChannel channel, ChannelType type) {
      net.dv8tion.jda.api.requests.restaction.ChannelAction<? extends GuildChannel> channelAction;
      long chId = 0;
      switch (this) {
        case FORUM -> channelAction = Util.nonNull(channel.getParentCategory()).createForumChannel(name);
        case NEWS -> channelAction = Util.nonNull(channel.getParentCategory()).createNewsChannel(name);
        case STAGE -> channelAction = Util.nonNull(channel.getParentCategory()).createStageChannel(name);
        case TEXT -> channelAction = Util.nonNull(channel.getParentCategory()).createTextChannel(name);
        case VOICE -> channelAction = Util.nonNull(channel.getParentCategory()).createVoiceChannel(name);
        default -> channelAction = null;
      }
      if (channelAction != null) {
        for (final Map.Entry<DiscordGroup, ChannelRolePattern> entry : type.getPattern().getData().entrySet()) {
          channelAction = channelAction.addRolePermissionOverride(entry.getKey().getDiscordId(), entry.getValue().getAllowed(), entry.getValue().getDenied());
        }
        channelAction.queue(createdChannel -> {
          DiscordChannelFactory.createChannel(Nunu.DiscordChannel.getChannel(createdChannel.getIdLong()));
        });
      }
    }
  }
}
