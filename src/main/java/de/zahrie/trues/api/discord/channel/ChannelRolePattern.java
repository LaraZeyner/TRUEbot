package de.zahrie.trues.api.discord.channel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.discord.permissible.PermissionPattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.Permission;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelRolePattern extends PermissionPattern {
  public static final ChannelRolePattern EMPTY = new ChannelRolePattern();
  public static final ChannelRolePattern HIDE = (ChannelRolePattern) new ChannelRolePattern(EMPTY).deny(Permission.VIEW_CHANNEL);
  public static final ChannelRolePattern HIDE_VOICE_BASE = new ChannelRolePattern(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_DEAF_OTHERS, Permission.VOICE_MUTE_OTHERS);
  public static final ChannelRolePattern HIDE_FULL = (ChannelRolePattern) new ChannelRolePattern(HIDE).deny(Permission.MESSAGE_SEND);
  public static final ChannelRolePattern VIEW_ONLY = (ChannelRolePattern) new ChannelRolePattern(HIDE_FULL).deny(Permission.VOICE_CONNECT);
  public static final ChannelRolePattern CONNECT_ONLY = (ChannelRolePattern) new ChannelRolePattern(EMPTY).deny(Permission.VOICE_SPEAK);
  public static final ChannelRolePattern EDIT = new ChannelRolePattern(Permission.MANAGE_CHANNEL);
  public static final ChannelRolePattern MANAGE = (ChannelRolePattern) new ChannelRolePattern(EDIT).allow(Permission.MANAGE_PERMISSIONS);
  public static final ChannelRolePattern MANAGE_TALK_BASE = new ChannelRolePattern(Permission.VOICE_SPEAK, Permission.VOICE_DEAF_OTHERS, Permission.VOICE_MUTE_OTHERS);

  private Set<Permission> revokeDenials = new HashSet<>();
  private boolean revokeAll = false;

  public ChannelRolePattern(Permission... permissions) {
    super(permissions);
  }

  public ChannelRolePattern(ChannelRolePattern rolePattern) {
    super(rolePattern.getAllowed().toArray(Permission[]::new));
    deny(rolePattern.getDenied().toArray(Permission[]::new));
  }

  public ChannelRolePattern revoke() {
    this.revokeAll = true;
    return this;
  }

  public ChannelRolePattern revoke(Permission... permissions) {
    this.revokeDenials = Arrays.stream(permissions).collect(Collectors.toSet());
    return this;
  }

  public ChannelRolePattern add(PermissionPattern pattern) {
    super.add(pattern);
    return this;
  }

  public ChannelRolePattern remove(Permission... permissions) {
    super.remove(permissions);
    return this;
  }

  public ChannelRolePattern remove(PermissionPattern pattern) {
    super.remove(pattern);
    return this;
  }
}
