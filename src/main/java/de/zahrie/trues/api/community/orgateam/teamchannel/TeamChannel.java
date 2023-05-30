package de.zahrie.trues.api.community.orgateam.teamchannel;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelType;
import de.zahrie.trues.api.discord.channel.ChannelType;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;

@Getter
@Setter
@Table(value = "discord_channel", department = "team")
public class TeamChannel extends DiscordChannel implements Entity<TeamChannel> {
  @Serial
  private static final long serialVersionUID = -1851145520721821488L;

  private final OrgaTeam orgaTeam; // orga_team
  private final TeamChannelType teamChannelType; // teamchannel_type

  public TeamChannel(long discordId, String name, ChannelType permissionType, net.dv8tion.jda.api.entities.channel.ChannelType channelType, OrgaTeam orgaTeam, TeamChannelType teamChannelType) {
    super(discordId, name, permissionType, channelType);
    this.orgaTeam = orgaTeam;
    this.teamChannelType = teamChannelType;
  }

  private TeamChannel(int id, long discordId, DiscordChannelType channelType, String name, ChannelType permissionType, OrgaTeam orgaTeam, TeamChannelType teamChannelType) {
    super(id, discordId, channelType, name, permissionType);
    this.orgaTeam = orgaTeam;
    this.teamChannelType = teamChannelType;
  }

  public static TeamChannel get(List<Object> objects) {
    return new TeamChannel(
        (int) objects.get(0),
        (long) objects.get(2),
        new SQLEnum<>(DiscordChannelType.class).of(objects.get(3)),
        (String) objects.get(4),
        new SQLEnum<>(ChannelType.class).of(objects.get(5)),
        new Query<>(OrgaTeam.class).entity( objects.get(6)),
        new SQLEnum<>(TeamChannelType.class).of(objects.get(7))
    );
  }

  @Override
  public TeamChannel create() {
    return new Query<>(TeamChannel.class).key("discord_id", discordId)
        .col("channel_type", channelType).col("channel_name", name).col("permission_type", permissionType)
        .col("orga_team", orgaTeam).col("teamchannel_type", teamChannelType)
        .insert(this);
  }

  @Override
  public boolean updatePermission(Role role) {
    final Role teamRole = orgaTeam.getRoleManager().getRole();
    if (teamRole.equals(role)) {
      updateForGroup(DiscordGroup.TEAM_ROLE_PLACEHOLDER);
      return true;
    }
    return super.updatePermission(role);
  }
}
