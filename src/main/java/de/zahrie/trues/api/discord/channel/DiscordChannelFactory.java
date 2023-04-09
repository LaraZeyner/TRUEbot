package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

@Log
public class DiscordChannelFactory {
  /**
   * Erhalte {@link DiscordChannel} vom GuildChannel
   */
  @NonNull
  public static DiscordChannel getDiscordChannel(@NonNull GuildChannel channel) {
    final DiscordChannel discordChannel = QueryBuilder.hql(DiscordChannel.class,
        "FROM DiscordChannel WHERE discordId = " + channel.getIdLong()).single();
    return Util.avoidNull(discordChannel, createChannel(channel));
  }

  /**
   * Erstelle Datenbankeintrag für Channel
   */
  @NonNull
  public static DiscordChannel createChannel(@NonNull GuildChannel channel) {
    if (channel instanceof ICategorizableChannel categorizableChannel) {
      final OrgaTeam orgaTeam = OrgaTeamFactory.getTeamFromChannel(categorizableChannel);
      if (orgaTeam != null) return createTeamChannel(channel, orgaTeam);
    }

    final PermissionChannelType permissionChannelType = determineChannelType(channel);
    final var discordChannel = new DiscordChannel(channel.getIdLong(), channel.getName(), permissionChannelType, channel.getType());
    Database.saveAndCommit(discordChannel);
    return discordChannel;
  }

  /**
   * Erstelle einen Teamchannel automatisch
   */
  @NonNull
  public static TeamChannel createTeamChannel(@NonNull GuildChannel channel, @NonNull OrgaTeam team) {
    final TeamChannelType channelType = TeamChannelType.fromChannel(channel);
    final PermissionChannelType permissionChannelType = channelType.getPermissionType();
    final var discordChannel = new TeamChannel(channel.getIdLong(), channel.getName(), permissionChannelType, channel.getType(), team, channelType);
    Database.saveAndCommit(discordChannel);
    return discordChannel;
  }

  public static void removeTeamChannel(@NonNull GuildChannel channel) {
    final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannel(channel);
    if (teamChannel != null) Database.removeAndCommit(teamChannel);
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
