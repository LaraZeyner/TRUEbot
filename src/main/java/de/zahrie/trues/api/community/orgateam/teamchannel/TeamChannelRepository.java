package de.zahrie.trues.api.community.orgateam.teamchannel;

import de.zahrie.trues.api.database.QueryBuilder;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.Nullable;

public class TeamChannelRepository {
  @Nullable
  public static TeamChannel getTeamChannelFromChannel(@NonNull GuildChannel channel) {
    return getTeamChannelFromChannelId(channel.getIdLong());
  }

  @Nullable
  public static TeamChannel getTeamChannelFromChannelId(long channelId) {
    return QueryBuilder.hql(TeamChannel.class, "FROM TeamChannel WHERE discordId = :channelId")
        .addParameter("channelId", channelId).single();
  }

}
