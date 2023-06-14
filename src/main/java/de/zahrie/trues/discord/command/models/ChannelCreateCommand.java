
package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.channel.ChannelType;
import de.zahrie.trues.api.discord.channel.DiscordChannelType;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.util.Util;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "channelcreate", descripion = "Channel erstellen", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "art", description = "Art des Channels", choices = {"Forum", "News", "Stage", "Text", "Voice"}),
    @Option(name = "name", description = "Name des Channels"),
    @Option(name = "typ", description = "Typ des Channels",
        choices = {"Content Intern", "Event", "Leaderboard", "Orga Intern", "Public", "Socials", "Staff Intern", "Team"})
})
public class ChannelCreateCommand extends SlashCommand {
  @Override
  @Msg("Der Auftrag wurde erfolgreich durchgeführt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final DiscordChannelType channelType = find("art").toEnum(DiscordChannelType.class);
    final var type = find("typ").toEnum(ChannelType.class);
    final String name = find("name").string();

    if (event.getChannel() instanceof ICategorizableChannel channel) {
      if (getInvokingMember().hasPermission(Util.nonNull(channel.getParentCategory()), Permission.MANAGE_CHANNEL)) {
        channelType.createChannel(name, channel.getParentCategory(), type);
        return sendMessage();
      }
    }
    return errorMessage();
  }
}
