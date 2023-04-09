package de.zahrie.trues.api.community.orgateam.teamchannel;

import java.util.Arrays;

import de.zahrie.trues.api.discord.channel.PermissionChannelType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

@RequiredArgsConstructor
@Getter
public enum TeamChannelType {
  CATEGORY(null, PermissionChannelType.TEAM_CHAT),
  CHAT(null, PermissionChannelType.TEAM_CHAT),
  INFO("team-info", PermissionChannelType.TEAM_CHAT),
  PRACTICE("Practice & PRM", PermissionChannelType.TEAM_VOICE),
  SCOUTING("scouting", PermissionChannelType.TEAM_CHAT),
  VOICE(null, PermissionChannelType.ORGA_INTERN_VOICE);

  private final String dafaultName;
  private final PermissionChannelType permissionType;

  public static TeamChannelType fromChannel(GuildChannel channel) {
    return Arrays.stream(TeamChannelType.values()).filter(teamChannelType -> teamChannelType.getDafaultName().equals(channel.getName()))
        .findFirst().orElse(channel instanceof VoiceChannel ? VOICE : (channel instanceof Category ? CATEGORY : CHAT));
  }
}

