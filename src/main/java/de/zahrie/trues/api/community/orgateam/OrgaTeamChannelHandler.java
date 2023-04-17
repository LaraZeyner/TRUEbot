package de.zahrie.trues.api.community.orgateam;

import java.util.Map;

import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelRepository;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.channel.ChannelRolePattern;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.channel.PermissionChannelType;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(StringUtils.class)
@Log
@RequiredArgsConstructor
public class OrgaTeamChannelHandler {
  private final OrgaTeam team;
  /**
   * Erhalte {@link TeamChannel} nach {@link TeamChannelType} <br>
   * Ist der Channel nicht vorhanden wird er erstellt
   */
  @Nullable
  public TeamChannel getChannelOf(TeamChannelType teamChannelType) {
    final TeamChannel channel = getExistingChannelOf(teamChannelType);
    return channel == null ? createChannel(teamChannelType) : channel;
  }

  private TeamChannel getExistingChannelOf(@NonNull TeamChannelType teamChannelType) {
    return QueryBuilder.hql(TeamChannel.class, "FROM TeamChannel WHERE orgaTeam = :team and teamChannelType = :channelType")
        .addParameters(Map.of("team", team, "channelType", teamChannelType)).single();
  }

  private String getCategoryName() {
    return team.getName() + " (" + team.getAbbreviation() + ")";
  }

  /**
   * Erstelle erforderliche Channel für ein Team
   */
  private TeamChannel createChannel(TeamChannelType channelType) {
    final TeamChannel category = getExistingChannelOf(TeamChannelType.CATEGORY);
    if (category == null) {
      createChannels();
    } else {
      switch (channelType) {
        case SCOUTING -> ChannelKind.TEXT.createTeamChannel((Category) category.getChannel(), TeamChannelType.SCOUTING, team);
        case INFO -> ChannelKind.TEXT.createTeamChannel((Category) category.getChannel(), TeamChannelType.INFO, team);
        case CHAT -> ChannelKind.TEXT.createTeamChannel((Category) category.getChannel(), TeamChannelType.CHAT, team);
        case VOICE, PRACTICE -> ChannelKind.VOICE.createTeamChannel((Category) category.getChannel(), TeamChannelType.PRACTICE, team);
        case CATEGORY -> createChannels();
      }
    }
    return getExistingChannelOf(channelType);
  }

  void createChannels() {
    Nunu.getInstance().getGuild().createCategory(team.getChannels().getCategoryName()).queue(category -> {
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.CHAT, team);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.INFO, team);
      ChannelKind.TEXT.createTeamChannel(category, TeamChannelType.SCOUTING, team);
      ChannelKind.VOICE.createTeamChannel(category, TeamChannelType.PRACTICE, team);
    });
  }

  /**
   * Erstelle einen Teamchannel automatisch
   */
  @NonNull
  public static TeamChannel createTeamChannelEntity(@NonNull GuildChannel channel, @NonNull OrgaTeam team) {
    final TeamChannelType channelType = TeamChannelType.fromChannel(channel);
    final PermissionChannelType permissionChannelType = channelType.getPermissionType();
    final TeamChannel teamChannel = new TeamChannel(channel.getIdLong(), channel.getName(), permissionChannelType, channel.getType(), team, channelType);
    Database.insert(teamChannel);
    Database.updateAndCommit(team);
    return teamChannel;
  }

  @RequiredArgsConstructor
  @Getter
  @ExtensionMethod(DiscordChannelFactory.class)
  public enum ChannelKind {
    FORUM(false),
    NEWS(false),
    STAGE(true),
    TEXT(false),
    VOICE(true);

    private final boolean voice;

    public void createChannel(String name, Category category, PermissionChannelType type) {
      getAction(name, category, type, null).queue();
    }

    public void createTeamChannel(Category category, TeamChannelType teamChannelType, OrgaTeam team) {
      getAction(teamChannelType.getDefaultName(), category, teamChannelType.getPermissionType(), team).queue();
    }

    private ChannelAction<? extends GuildChannel> getAction(String name, Category category, PermissionChannelType type, OrgaTeam team) {
      ChannelAction<? extends GuildChannel> channelAction = switch (this) {
        case FORUM -> Util.nonNull(category).createForumChannel(name);
        case NEWS -> Util.nonNull(category).createNewsChannel(name);
        case STAGE -> Util.nonNull(category).createStageChannel(name);
        case TEXT -> Util.nonNull(category).createTextChannel(name);
        case VOICE -> Util.nonNull(category).createVoiceChannel(name);
      };
      for (Map.Entry<DiscordGroup, ChannelRolePattern> entry : type.getPattern().getData().entrySet()) {
        final DiscordGroup discordGroup = entry.getKey();
        if (team == null) {
          final TeamChannel teamChannel = TeamChannelRepository.getTeamChannelFromChannel(category);
          if (teamChannel != null) team = teamChannel.getOrgaTeam();
        }
        long roleId = discordGroup.getDiscordId();
        if (discordGroup.equals(DiscordGroup.TEAM_ROLE_PLACEHOLDER) && team != null) roleId = team.getRoleManager().getRole().getIdLong();
        if (roleId == -1) continue;

        channelAction = channelAction.addRolePermissionOverride(roleId, entry.getValue().getAllowed(), entry.getValue().getDenied());
      }
      return channelAction;
    }
  }
}
