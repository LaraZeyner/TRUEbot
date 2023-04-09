package de.zahrie.trues.api.discord.channel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.Util;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "DiscordChannel")
@Table(name = "discord_channel")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "orga_team")
public class DiscordChannel implements Serializable {
  @Serial
  private static final long serialVersionUID = -2307398301886813719L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;

  @Column(name = "discord_id", nullable = false)
  private long discordId;

  @Column(name = "channel_name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "permission_type", nullable = false)
  private PermissionChannelType permissionType;

  @Enumerated(EnumType.STRING)
  @Column(name = "channel_type", nullable = false)
  private ChannelType channelType;

  public DiscordChannel(long discordId, String name, PermissionChannelType permissionType, ChannelType channelType) {
    this.discordId = discordId;
    this.name = name;
    this.permissionType = permissionType;
    this.channelType = channelType;
  }


  public IPermissionContainer getChannel() {
    return (IPermissionContainer) Nunu.DiscordChannel.getChannel(discordId);
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

  protected void updateForGroup(DiscordGroup group) {
    uFr(group.getRole(), group);
  }

  protected void uFr(Role role, DiscordGroup group) {
    final ChannelRolePattern rolePattern = permissionType.getPattern().getData().get(group);
    final PermissionOverride permissionOverride = Util.nonNull(getChannel().getPermissionOverride(role), "Fehler mit Channel oder User");
    final Set<Permission> allowed = rolePattern.getAllowed();
    final Set<Permission> pattern = permissionType.getPattern().getData().get(DiscordGroup.EVERYONE).getDenied();
    if (!rolePattern.isRevokeAll()) {
      pattern.retainAll(rolePattern.getRevokeDenials());
    }
    allowed.addAll(pattern);
    final Set<Permission> denied = rolePattern.getDenied();
    allowed.removeAll(denied);
    if (getChannel() instanceof AudioChannel) {
      allowed.remove(Permission.VIEW_CHANNEL);
      denied.remove(Permission.VIEW_CHANNEL);
    }
    permissionOverride.getManager().setPermissions(allowed, denied).queue();
  }
}
