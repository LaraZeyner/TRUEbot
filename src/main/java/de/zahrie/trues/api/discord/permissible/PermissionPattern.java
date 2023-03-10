package de.zahrie.trues.api.discord.permissible;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import net.dv8tion.jda.api.Permission;

@Data
public class PermissionPattern {
  protected static final PermissionPattern CHANNEL_ACCESS = new PermissionPattern(
      Permission.MESSAGE_HISTORY,
      Permission.VIEW_CHANNEL,
      Permission.MESSAGE_ADD_REACTION,
      Permission.MESSAGE_EXT_EMOJI,
      Permission.MESSAGE_EXT_STICKER,
      Permission.USE_APPLICATION_COMMANDS
  );

  protected static final PermissionPattern CHANNEL_INTERACT = new PermissionPattern(
      Permission.MESSAGE_ATTACH_FILES,
      Permission.MESSAGE_EMBED_LINKS,
      Permission.MESSAGE_SEND,
      Permission.MESSAGE_SEND_IN_THREADS,
      Permission.REQUEST_TO_SPEAK,
      Permission.VOICE_CONNECT,
      Permission.CREATE_PUBLIC_THREADS,
      Permission.CREATE_PRIVATE_THREADS
  ).addPattern(CHANNEL_ACCESS);

  protected static final PermissionPattern CHANNEL_INTERACT_TALK = new PermissionPattern(
      Permission.VOICE_SPEAK,
      Permission.VOICE_START_ACTIVITIES,
      Permission.VOICE_STREAM,
      Permission.VOICE_USE_VAD
  ).addPattern(CHANNEL_INTERACT);

  protected static final PermissionPattern CHANNEL_INTERACT_ADVANCED = new PermissionPattern(
      Permission.VOICE_MOVE_OTHERS,
      Permission.MESSAGE_MENTION_EVERYONE
  ).addPattern(CHANNEL_INTERACT_TALK);

  protected static final PermissionPattern CHANNEL_INTERACT_MODERATE = new PermissionPattern(
      Permission.MANAGE_CHANNEL,
      Permission.MANAGE_THREADS,
      Permission.MESSAGE_TTS,
      Permission.PRIORITY_SPEAKER,
      Permission.VOICE_MUTE_OTHERS,
      Permission.VOICE_DEAF_OTHERS
  ).addPattern(CHANNEL_INTERACT_TALK);

  protected static final PermissionPattern GUILD_VIEW = new PermissionPattern(
      Permission.VIEW_AUDIT_LOGS,
      Permission.VIEW_GUILD_INSIGHTS
  );

  protected static final PermissionPattern GUILD_MODERATE = new PermissionPattern(
      Permission.MESSAGE_MANAGE,
      Permission.MODERATE_MEMBERS,
      Permission.NICKNAME_MANAGE
  ).addPattern(GUILD_VIEW);

  protected static final PermissionPattern GUILD_ADMINISTRATE = new PermissionPattern(
      Permission.KICK_MEMBERS
  ).addPattern(GUILD_MODERATE);

  protected static final PermissionPattern CONTENT_CREATION = new PermissionPattern(
      Permission.MANAGE_EMOJIS_AND_STICKERS,
      Permission.MANAGE_EVENTS,
      Permission.MANAGE_WEBHOOKS
  );

  protected static final PermissionPattern ALL = new PermissionPattern(
      Permission.MANAGE_SERVER,
      Permission.MANAGE_ROLES,
      Permission.MANAGE_PERMISSIONS,
      Permission.NICKNAME_CHANGE,
      Permission.CREATE_INSTANT_INVITE,
      Permission.ADMINISTRATOR
  ).addPattern(CHANNEL_INTERACT_MODERATE).addPattern(GUILD_ADMINISTRATE).addPattern(CONTENT_CREATION);


  protected final Set<Permission> permissions;

  public PermissionPattern(Permission... permissions) {
    this.permissions = Arrays.stream(permissions).collect(Collectors.toSet());
  }

  public PermissionPattern addPattern(PermissionPattern pattern) {
    permissions.addAll(pattern.getPermissions());
    return this;
  }

}
