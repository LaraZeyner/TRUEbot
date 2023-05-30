package de.zahrie.trues.api.community.orgateam;

import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.channel.ChannelType;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import de.zahrie.trues.util.io.log.DevInfo;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Role;
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
  public TeamChannel get(TeamChannelType teamChannelType) {
    final TeamChannel channel = getExistingChannelOf(teamChannelType);
    return channel == null ? createChannel(teamChannelType) : channel;
  }

  void updateChannels() {
    final String categoryName = getCategoryName();
    final TeamChannel teamChannel = get(TeamChannelType.CATEGORY);
    if (teamChannel == null) {
      final RuntimeException e = new NullPointerException("Team Channel sollte bereits erstellt sein!");
      new DevInfo().severe(e);
      throw new RuntimeException(e);
    }

    teamChannel.setName(categoryName);
    teamChannel.getChannel().getManager().setName(categoryName).queue();
  }

  private TeamChannel getExistingChannelOf(@NonNull TeamChannelType teamChannelType) {
    return new Query<>(TeamChannel.class).where("orga_team", team).and("teamchannel_type", teamChannelType).entity();
  }

  String getCategoryName() {
    return team.getName() + " (" + team.getAbbreviation() + ")";
  }

  /**
   * Erstelle erforderliche Channel für ein Team
   */
  private TeamChannel createChannel(TeamChannelType channelType) {
    final TeamChannel category = getExistingChannelOf(TeamChannelType.CATEGORY);
    if (category == null) createChannels();
    else {
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
    final ChannelType permissionChannelType = channelType.getPermissionType();
    return new TeamChannel(channel.getIdLong(), channel.getName(), permissionChannelType, channel.getType(), team, channelType).forceCreate();
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

    public void createChannel(String name, Category category, ChannelType type) {
      getAction(name, category, type, null).queue();
    }

    public void createTeamChannel(Category category, TeamChannelType teamChannelType, OrgaTeam team) {
      getAction(teamChannelType.getDefaultName(), category, teamChannelType.getPermissionType(), team).queue();
    }

    private ChannelAction<? extends GuildChannel> getAction(String name, Category category, ChannelType type, OrgaTeam team) {
      final ChannelAction<? extends GuildChannel> channelAction = switch (this) {
        case FORUM -> Util.nonNull(category).createForumChannel(name);
        case NEWS -> Util.nonNull(category).createNewsChannel(name);
        case STAGE -> Util.nonNull(category).createStageChannel(name);
        case TEXT -> Util.nonNull(category).createTextChannel(name);
        case VOICE -> Util.nonNull(category).createVoiceChannel(name);
      };

      channelAction.clearPermissionOverrides().queue();
      for (ChannelType.APermissionOverride permission : type.getPermissions()) {
        IPermissionHolder permissionHolder = permission.permissionHolder();
        if (permissionHolder instanceof Role role && OrgaTeamFactory.isRoleOfTeam(role)) {
          permissionHolder = team.getRoleManager().getRole();
        }

        channelAction.addPermissionOverride(permissionHolder, permission.getAllowed(), permission.getDenied()).queue();
      }
      return channelAction;
    }
  }
}
