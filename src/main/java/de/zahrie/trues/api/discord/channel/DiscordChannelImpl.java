package de.zahrie.trues.api.discord.channel;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.channel.ChannelType;

@Getter
@Setter
@Table(value = "discord_channel", department = "other")
@Log
public class DiscordChannelImpl extends DiscordChannel implements Entity<DiscordChannelImpl> {
  @Serial
  private static final long serialVersionUID = -495599946883173951L;

  public DiscordChannelImpl(long discordId, String name, PermissionChannelType permissionType, ChannelType channelType) {
    super(discordId, name, permissionType, channelType);
  }

  private DiscordChannelImpl(int id, long discordId, DiscordChannelType channelType, String name, PermissionChannelType permissionType) {
    super(id, discordId, channelType, name, permissionType);
  }

  public static DiscordChannelImpl get(List<Object> objects) {
    return new DiscordChannelImpl(
        (int) objects.get(0),
        (long) objects.get(2),
        new SQLEnum<>(DiscordChannelType.class).of(objects.get(3)),
        (String) objects.get(4),
        new SQLEnum<>(PermissionChannelType.class).of(objects.get(5))
    );
  }

  @Override
  public DiscordChannelImpl create() {
    return new Query<>(DiscordChannelImpl.class).key("discord_id", discordId)
        .col("channel_type", channelType).col("channel_name", name).col("permission_type", permissionType)
        .insert(this);
  }
}
