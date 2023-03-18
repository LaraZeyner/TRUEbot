package de.zahrie.trues.api.discord.channel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import de.zahrie.trues.api.discord.group.CustomDiscordRole;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.discord.Nunu;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.util.util.Util;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "DiscordChannel")
@Table(name = "discord_channel")
@NamedQuery(name = "DiscordChannel.fromDiscordId", query = "FROM DiscordChannel WHERE discordId = :discordId")
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
  @Column(name = "channel_type", nullable = false)
  private ChannelType type;

  @OneToOne(mappedBy = "category")
  @Getter(AccessLevel.NONE)
  private OrgaTeam categoryTeam;

  @OneToOne(mappedBy = "chat")
  @Getter(AccessLevel.NONE)
  private OrgaTeam chatChannelTeam;

  @OneToOne(mappedBy = "voice")
  @Getter(AccessLevel.NONE)
  private OrgaTeam voiceChannelTeam;

  @OneToOne(mappedBy = "info")
  @Getter(AccessLevel.NONE)
  private OrgaTeam infoChannelTeam;

  public DiscordChannel(long discordId, String name, ChannelType type) {
    this.discordId = discordId;
    this.name = name;
    this.type = type;
  }

  public OrgaTeam getTeam() {
    if (categoryTeam != null) return categoryTeam;
    if (chatChannelTeam != null) return chatChannelTeam;
    if (voiceChannelTeam != null) return voiceChannelTeam;
    if (infoChannelTeam != null) return infoChannelTeam;
    return null;
  }

  public IPermissionContainer getChannel() {
    return (IPermissionContainer) Nunu.DiscordChannel.getChannel(discordId);
  }

  public void updatePermissions() {
    final ChannelType.ChannelPattern channelPattern = type.getPattern();
    channelPattern.getData().forEach(((group, rolePattern) -> updateForGroup(group)));
  }

  public boolean updatePermission(Role role) {
    final CustomDiscordRole customRole = getTeam().getCustomRole();
    if (getTeam() != null && customRole.getRole().equals(role)) {
      updateForGroup(DiscordGroup.TEAM_ROLE_PLACEHOLDER);
    } else {
      final DiscordGroup group = DiscordGroup.of(role.getIdLong());
      if (group == null) return false;
      updateForGroup(group);
    }
    return true;
  }

  private void updateForGroup(DiscordGroup group) {
    final ChannelRolePattern rolePattern = type.getPattern().getData().get(group);
    final Role role = group.equals(DiscordGroup.TEAM_ROLE_PLACEHOLDER) ? getTeam().getRole() : group.getRole();
    uFr(role, rolePattern);
  }

  private void uFr(Role role, ChannelRolePattern rolePattern) {
    final PermissionOverride permissionOverride = Util.nonNull(getChannel().getPermissionOverride(role), "Fehler mit Channel oder User");
    final Set<Permission> allowed = rolePattern.getAllowed();
    final Set<Permission> pattern = type.getPattern().getData().get(DiscordGroup.EVERYONE).getDenied();
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
