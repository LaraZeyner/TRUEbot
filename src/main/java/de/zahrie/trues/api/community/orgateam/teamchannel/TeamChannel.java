package de.zahrie.trues.api.community.orgateam.teamchannel;

import java.io.Serial;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelType;
import de.zahrie.trues.api.discord.channel.PermissionChannelType;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;

@Getter
@Setter
@Table(value = "discord_channel", department = "team")
public class TeamChannel extends DiscordChannel implements Entity<TeamChannel> {
  @Serial
  private static final long serialVersionUID = -1851145520721821488L;

  private final OrgaTeam orgaTeam; // orga_team
  private final TeamChannelType teamChannelType; // teamchannel_type

  public TeamChannel(long discordId, String name, PermissionChannelType permissionType, ChannelType channelType, OrgaTeam orgaTeam, TeamChannelType teamChannelType) {
    super(discordId, name, permissionType, channelType);
    this.orgaTeam = orgaTeam;
    this.teamChannelType = teamChannelType;
  }

  private TeamChannel(int id, long discordId, DiscordChannelType channelType, String name, PermissionChannelType permissionType, OrgaTeam orgaTeam, TeamChannelType teamChannelType) {
    super(id, discordId, channelType, name, permissionType);
    this.orgaTeam = orgaTeam;
    this.teamChannelType = teamChannelType;
  }

  public static TeamChannel get(Object[] objects) {
    return new TeamChannel(
        (int) objects[0],
        (long) objects[2],
        new SQLEnum<DiscordChannelType>().of(objects[3]),
        (String) objects[4],
        new SQLEnum<PermissionChannelType>().of(objects[5]),
        new Query<OrgaTeam>().entity( objects[6]),
        new SQLEnum<TeamChannelType>().of(objects[7])
    );
  }

  @Override
  public TeamChannel create() {
    return new Query<TeamChannel>().key("discord_id", discordId).key("department", "team")
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

  @Override
  public void updateForGroup(DiscordGroup group) {
    final Role role = orgaTeam.getRoleManager().getRole();
    uFr(role, group);
  }
}
