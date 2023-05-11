package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamChannelHandler;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.util.StringUtils;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

@Log
@ExtensionMethod(StringUtils.class)
public class DiscordChannelFactory {
  /**
   * Erhalte {@link DiscordChannelImpl} vom GuildChannel <br>
   * Wenn noch nicht vorhanden erstelle Datenbankeintrag
   */
  @NonNull
  public static DiscordChannel getDiscordChannel(@NonNull GuildChannel channel) {
    final DiscordChannel discordChannel = new Query<>(DiscordChannel.class).where("discord_id", channel.getIdLong()).entity();
    return discordChannel != null ? discordChannel : createChannel(channel);
  }

  /**
   * Erstelle Channeleintrag in Datenbank, sofern noch nicht vorhanden
   */
  @NonNull
  private static DiscordChannel createChannel(@NonNull GuildChannel channel) {
    OrgaTeam orgaTeam = OrgaTeamFactory.getTeamFromChannel(channel);
    if (orgaTeam == null && channel.getName().contains(" (")) {
      final String categoryAbbr = channel.getName().between(" (", ")");
      orgaTeam = new Query<>(OrgaTeam.class).where("team_abbr_created", categoryAbbr).entity();
    }

    return orgaTeam != null ? OrgaTeamChannelHandler.createTeamChannelEntity(channel, orgaTeam) :
        new DiscordChannelImpl(channel.getIdLong(), channel.getName(), determineChannelType(channel), channel.getType()).forceCreate();
  }

  public static void removeTeamChannel(@NonNull GuildChannel channel) {
    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannel(channel);
    if (teamChannel != null) teamChannel.forceDelete();
  }

  private static PermissionChannelType determineChannelType(GuildChannel initialChannel) {
    if (!(initialChannel instanceof final ICategorizableChannel channel)) return PermissionChannelType.PUBLIC;

    final OrgaTeam team = OrgaTeamFactory.getTeamFromChannel(channel);
    if (team != null) return initialChannel instanceof AudioChannel ? PermissionChannelType.TEAM_VOICE : PermissionChannelType.TEAM_CHAT;

    final Category category = channel.getParentCategory();
    if (category == null) return PermissionChannelType.PUBLIC;

    return switch (category.getName()) {
      case "Social Media" -> PermissionChannelType.SOCIALS;
      case "Events" -> PermissionChannelType.EVENTS;
      case "Orga Intern" -> channel instanceof AudioChannel ? PermissionChannelType.ORGA_INTERN_VOICE : PermissionChannelType.ORGA_INTERN;
      case "Content" -> channel instanceof AudioChannel ? PermissionChannelType.CONTENT_INTERN_VOICE : PermissionChannelType.CONTENT_INTERN;
      default -> PermissionChannelType.PUBLIC;
    };
  }
}
