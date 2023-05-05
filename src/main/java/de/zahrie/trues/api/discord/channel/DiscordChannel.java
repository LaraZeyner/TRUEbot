package de.zahrie.trues.api.discord.channel;

import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;

@AllArgsConstructor
@Getter
@Table("discord_channel")
@Log
public abstract class DiscordChannel implements ADiscordChannel, Id {
  protected int id; // discord_channel_id
  protected final long discordId; // discord_id
  protected final DiscordChannelType channelType; // channel_type
  protected String name; // channel_name
  protected PermissionChannelType permissionType; // permission_type

  public DiscordChannel(long discordId, String name, PermissionChannelType permissionType, ChannelType channelType) {
    this.discordId = discordId;
    this.name = name;
    this.permissionType = permissionType;
    this.channelType = DiscordChannelType.valueOf(channelType.name());
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
    new Query<DiscordChannel>().col("channel_name", name).update(id);
    Database.connection().commit();
  }

  public void setPermissionType(PermissionChannelType permissionType) {
    this.permissionType = permissionType;
    new Query<DiscordChannel>().col("permission_type", permissionType).update(id);
  }

  public void updatePermissions() {
    final PermissionChannelType.ChannelPattern channelPattern = permissionType.getPattern();
    channelPattern.getData().forEach(((group, rolePattern) -> updateForGroup(group)));
  }

  public boolean updatePermission(Role role) {
    final DiscordGroup group = DiscordGroup.of(role);
    if (group == null) return false;
    updateForGroup(group);
    return true;
  }
}
