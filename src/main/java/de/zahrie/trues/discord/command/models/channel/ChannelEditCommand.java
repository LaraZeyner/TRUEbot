
package de.zahrie.trues.discord.command.models.channel;

import de.zahrie.trues.api.discord.channel.ChannelType;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.util.Util;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "edit", descripion = "Channel bearbeiten", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "typ", description = "Typ des Channels", required = false, choices = {"Socials", "Event", "Leaderboard", "Orga Intern", "Staff Intern", "Content Intern"})
})
public class ChannelEditCommand extends SlashCommand {
  @Override
  @Msg("Der Auftrag wurde erfolgreich durchgeführt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final ChannelType type = get("typ", ChannelType.class);
    if (event.getChannel() instanceof ICategorizableChannel channel) {
      if (getInvokingMember().hasPermission(Util.nonNull(channel.getParentCategory()), Permission.MANAGE_PERMISSIONS)) {
        final DiscordChannel discordChannel = DiscordChannelFactory.getDiscordChannel(channel);
        discordChannel.setType(type);
        Database.save(type);
        discordChannel.updatePermissions();
      }
    }
    return errorMessage();
  }
}
