package de.zahrie.trues.api.community.orgateam.teamchannel;

import java.util.Arrays;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.discord.channel.ChannelType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

@RequiredArgsConstructor
@Getter
@Listing(Listing.ListingType.LOWER)
public enum TeamChannelType {
  CATEGORY(null, ChannelType.TEAM_CHAT),
  CHAT("\uD83D\uDCAC︱team-chat", ChannelType.TEAM_CHAT),
  INFO("\uD83D\uDCCB︱team-info", ChannelType.TEAM_CHAT),
  PRACTICE("Practice & PRM", ChannelType.TEAM_VOICE),
  SCOUTING("\uD83D\uDD0E︱scouting", ChannelType.TEAM_CHAT),
  VOICE(null, ChannelType.ORGA_INTERN_VOICE);

  private final String defaultName;
  private final ChannelType permissionType;

  public static TeamChannelType fromChannel(GuildChannel channel) {
    return Arrays.stream(TeamChannelType.values()).filter(teamChannelType -> channel.getName().equals(teamChannelType.getDefaultName()))
        .findFirst().orElse(channel instanceof VoiceChannel ? VOICE : (channel instanceof Category ? CATEGORY : CHAT));
  }
}

