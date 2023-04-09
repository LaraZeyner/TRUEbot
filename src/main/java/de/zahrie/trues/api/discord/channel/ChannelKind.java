package de.zahrie.trues.api.discord.channel;

import java.util.Map;

import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

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
    getAction(name, category, type).queue();
  }

  public void createTeamChannel(Category category, TeamChannelType teamChannelType) {
    getAction(teamChannelType.getDafaultName(), category, teamChannelType.getPermissionType()).queue();
  }

  private ChannelAction<? extends GuildChannel> getAction(String name, Category category, PermissionChannelType type) {
    ChannelAction<? extends GuildChannel> channelAction = switch (this) {
      case FORUM -> Util.nonNull(category).createForumChannel(name);
      case NEWS -> Util.nonNull(category).createNewsChannel(name);
      case STAGE -> Util.nonNull(category).createStageChannel(name);
      case TEXT -> Util.nonNull(category).createTextChannel(name);
      case VOICE -> Util.nonNull(category).createVoiceChannel(name);
    };
    for (Map.Entry<DiscordGroup, ChannelRolePattern> entry : type.getPattern().getData().entrySet()) {
      channelAction = channelAction.addRolePermissionOverride(entry.getKey().getDiscordId(), entry.getValue().getAllowed(), entry.getValue().getDenied());
    }
    return channelAction;
  }
}
