
package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.channel.ChannelType;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.PermissionRole;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "channeledit", descripion = "Channel bearbeiten", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "typ", description = "Typ des Channels", required = false, choices = {"Socials", "Event", "Leaderboard", "Orga Intern", "Staff Intern", "Content Intern"})
})
@ExtensionMethod(DiscordChannelFactory.class)
public class ChannelEditCommand extends SlashCommand {
  @Override
  @Msg("Der Auftrag wurde erfolgreich durchgeführt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final ChannelType type = find("typ").toEnum(ChannelType.class);
    if (event.getChannel() instanceof ICategorizableChannel channel) {
      final Category category = channel.getParentCategory();
      if (category == null) return false;

      if (getInvoker().getActiveGroups().contains(DiscordGroup.ADMIN)) {
        final DiscordChannel discordChannel = channel.getDiscordChannel();
        discordChannel.setPermissionType(type);
        discordChannel.updatePermissions();
      }

      final OrgaTeam teamFromChannel = OrgaTeamFactory.getTeamFromChannel(category);
      if (teamFromChannel != null && MembershipFactory.getMembershipOf(getInvoker(), teamFromChannel) != null) {
        final DiscordChannel discordChannel = channel.getDiscordChannel();
        discordChannel.setPermissionType(type);
        discordChannel.updatePermissions();
      }
    }
    return errorMessage();
  }
}
