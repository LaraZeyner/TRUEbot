
package de.zahrie.trues.discord.command.models.channel;

import de.zahrie.trues.api.discord.channel.PermissionChannelType;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.Util;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "channeledit", descripion = "Channel bearbeiten", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "typ", description = "Typ des Channels", required = false, choices = {"Socials", "Event", "Leaderboard", "Orga Intern", "Staff Intern", "Content Intern"})
})
@ExtensionMethod(DiscordChannelFactory.class)
public class ChannelEditCommand extends SlashCommand {
  @Override
  @Msg("Der Auftrag wurde erfolgreich durchgeführt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final PermissionChannelType type = find("typ").toEnum(PermissionChannelType.class);
    if (event.getChannel() instanceof ICategorizableChannel channel) {
      if (getInvokingMember().hasPermission(Util.nonNull(channel.getParentCategory()), Permission.MANAGE_PERMISSIONS)) {
        final DiscordChannel discordChannel = channel.getDiscordChannel();
        discordChannel.setPermissionType(type);
        Database.update(type);
        discordChannel.updatePermissions();
      }
    }
    return errorMessage();
  }
}
