package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamChannelHandler;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
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
   * Erhalte {@link DiscordChannel} vom GuildChannel <br>
   * Wenn noch nicht vorhanden erstelle Datenbankeintrag
   */
  @NonNull
  public static DiscordChannel getDiscordChannel(@NonNull GuildChannel channel) {
    final TeamChannel teamChannel = QueryBuilder.hql(TeamChannel.class,
        "FROM TeamChannel WHERE discordId = :discordId").addParameter("discordId", channel.getIdLong()).single();
    if (teamChannel != null) return teamChannel;

    final DiscordChannel discordChannel = QueryBuilder.hql(DiscordChannel.class,
        "FROM DiscordChannel WHERE discordId = :discordId").addParameter("discordId", channel.getIdLong()).single();
    if (discordChannel != null) return discordChannel;

    return createChannel(channel);
  }

  /**
   * Erstelle Channeleintrag in Datenbank, sofern noch nicht vorhanden
   */
  @NonNull
  private static DiscordChannel createChannel(@NonNull GuildChannel channel) {
    OrgaTeam orgaTeam = OrgaTeamFactory.getTeamFromChannel(channel);
    if (orgaTeam == null && channel.getName().contains(" (")) {
      final String categoryAbbr = channel.getName().between(" (", ")");
      orgaTeam = QueryBuilder.hql(OrgaTeam.class, "FROM OrgaTeam WHERE abbreviationCreation = :abbr").addParameter("abbr", categoryAbbr).single();
    }

    if (orgaTeam != null) return OrgaTeamChannelHandler.createTeamChannelEntity(channel, orgaTeam);

    final var discordChannel = new DiscordChannel(channel.getIdLong(), channel.getName(), determineChannelType(channel), channel.getType());
    Database.insertAndCommit(discordChannel);
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
