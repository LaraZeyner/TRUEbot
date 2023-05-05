package de.zahrie.trues.api.discord.channel;

import java.util.Set;

import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.util.Nunu;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public interface ADiscordChannel {
  long getDiscordId();
  DiscordChannelType getChannelType();
  String getName();
  void setName(String name);
  PermissionChannelType getPermissionType();
  void setPermissionType(PermissionChannelType permissionType);

  default void updatePermissions() {
    final PermissionChannelType.ChannelPattern channelPattern = getPermissionType().getPattern();
    channelPattern.getData().forEach(((group, rolePattern) -> updateForGroup(group)));
  }

  default void updateForGroup(DiscordGroup group) {
    uFr(group.getRole(), group);
  }

  default void uFr(Role role, DiscordGroup group) {
    final ChannelRolePattern rolePattern = getPermissionType().getPattern().getData().get(group);
    final PermissionOverride override = getChannel().getPermissionOverride(role);
    if (override == null) {
      getChannel().getManager().putPermissionOverride(role, rolePattern.getAllowed(), rolePattern.getDenied()).queue();
      System.err.println("Override für " + role.getName() + " in " + getName() + " nicht vorhanden.");
      return;
    }
    final Set<Permission> allowed = rolePattern.getAllowed();
    final Set<Permission> pattern = getPermissionType().getPattern().getData().get(DiscordGroup.EVERYONE).getDenied();
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
    override.getManager().setPermissions(allowed, denied).queue();
  }

  default IPermissionContainer getChannel() {
    return (IPermissionContainer) Nunu.DiscordChannel.getChannel(getDiscordId());
  }
}
