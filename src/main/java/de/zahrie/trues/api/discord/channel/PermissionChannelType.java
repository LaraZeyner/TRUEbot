package de.zahrie.trues.api.discord.channel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import net.dv8tion.jda.api.Permission;

@RequiredArgsConstructor
@Getter
@Listing(Listing.ListingType.LOWER)
public enum PermissionChannelType {
  PUBLIC(ChannelPattern.builder().build(), true, true),
  SOCIALS(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.VIEW_ONLY)
      .assign(DiscordGroup.EVENT, ChannelRolePattern.EMPTY.revoke())
      .assign(DiscordGroup.EVENT_LEAD, ChannelRolePattern.EDIT.revoke())
      .assign(DiscordGroup.SOCIAL_MEDIA_LEAD, ChannelRolePattern.EDIT.revoke())
      .assign(DiscordGroup.ADMIN, ChannelRolePattern.EDIT.revoke())
      .build(), true, true),
  EVENTS(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.VIEW_ONLY)
      .assign(DiscordGroup.EVENT_PLANNING, ChannelRolePattern.EDIT.revoke())
      .assign(DiscordGroup.EVENT_LEAD, ChannelRolePattern.MANAGE.revoke())
      .assign(DiscordGroup.SOCIAL_MEDIA_LEAD, ChannelRolePattern.MANAGE.revoke())
      .assign(DiscordGroup.ADMIN, ChannelRolePattern.MANAGE.revoke())
      .build(), false, true),
  LEADERBOARD(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.HIDE_FULL)
      .assign(DiscordGroup.ORGA_MEMBER, ChannelRolePattern.VIEW_ONLY.revoke())
      .assign(DiscordGroup.SOCIAL_MEDIA_LEAD, ChannelRolePattern.MANAGE.revoke())
      .assign(DiscordGroup.ADMIN, ChannelRolePattern.MANAGE.revoke())
      .build(), false, true),
  ORGA_INTERN(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.HIDE)
      .assign(DiscordGroup.TRYOUT, ChannelRolePattern.EMPTY.revoke())
      .assign(DiscordGroup.ORGA_MEMBER, ChannelRolePattern.EMPTY.revoke())
      .build(), false, true),
  ORGA_INTERN_VOICE(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.VIEW_ONLY)
      .assign(DiscordGroup.TRYOUT, ChannelRolePattern.EMPTY.revoke())
      .assign(DiscordGroup.ORGA_MEMBER, ChannelRolePattern.EMPTY.revoke())
      .build(), false, true),
  STAFF_INTERN(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.HIDE)
      .assign(DiscordGroup.STAFF, ChannelRolePattern.EMPTY.revoke())
      .build(), true, true),
  CONTENT_INTERN(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.HIDE)
      .assign(DiscordGroup.ORGA_MEMBER, ChannelRolePattern.CONNECT_ONLY)
      .assign(DiscordGroup.STAFF, ChannelRolePattern.CONNECT_ONLY)
      .assign(DiscordGroup.EVENT, ChannelRolePattern.MANAGE_TALK_BASE.revoke())
      .assign(DiscordGroup.SOCIAL_MEDIA_LEAD, ChannelRolePattern.MANAGE.revoke())
      .assign(DiscordGroup.EVENT_LEAD, ChannelRolePattern.MANAGE.revoke())
      .assign(DiscordGroup.ADMIN, ChannelRolePattern.MANAGE.revoke())
      .build(), true, true),
  CONTENT_INTERN_VOICE(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.VIEW_ONLY)
      .assign(DiscordGroup.ORGA_MEMBER, ChannelRolePattern.CONNECT_ONLY)
      .assign(DiscordGroup.STAFF, ChannelRolePattern.CONNECT_ONLY)
      .assign(DiscordGroup.EVENT, ChannelRolePattern.MANAGE_TALK_BASE.revoke())
      .assign(DiscordGroup.SOCIAL_MEDIA_LEAD, ChannelRolePattern.MANAGE.revoke())
      .assign(DiscordGroup.EVENT_LEAD, ChannelRolePattern.MANAGE.revoke())
      .assign(DiscordGroup.ADMIN, ChannelRolePattern.MANAGE.revoke())
      .build(), true, true),
  TEAM_CHAT(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.HIDE.remove(ChannelRolePattern.HIDE_VOICE_BASE))
      .assign(DiscordGroup.ORGA_MEMBER, ChannelRolePattern.CONNECT_ONLY)
      .assign(DiscordGroup.TEAM_ROLE_PLACEHOLDER, ChannelRolePattern.MANAGE.add(ChannelRolePattern.MANAGE_TALK_BASE).revoke())
      .assign(DiscordGroup.STAFF, ChannelRolePattern.EDIT.revoke(Permission.VIEW_CHANNEL, Permission.VOICE_SPEAK))
      .assign(DiscordGroup.ADMIN, ChannelRolePattern.EMPTY.revoke(Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
      .build(), false, false),
  TEAM_VOICE(ChannelPattern.builder()
      .assign(DiscordGroup.EVERYONE, ChannelRolePattern.EMPTY.remove(ChannelRolePattern.HIDE_VOICE_BASE))
      .assign(DiscordGroup.ORGA_MEMBER, ChannelRolePattern.CONNECT_ONLY)
      .assign(DiscordGroup.TEAM_ROLE_PLACEHOLDER, ChannelRolePattern.MANAGE.add(ChannelRolePattern.MANAGE_TALK_BASE).revoke())
      .assign(DiscordGroup.STAFF, ChannelRolePattern.EDIT.revoke(Permission.VOICE_SPEAK))
      .assign(DiscordGroup.ADMIN, ChannelRolePattern.EMPTY.revoke(Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
      .build(), false, true);


  private final ChannelPattern pattern;
  private final boolean fixed;
  private final boolean template;

  public static List<PermissionChannelType> getTemplates() {
    return Arrays.stream(PermissionChannelType.values()).filter(PermissionChannelType::isTemplate).toList();
  }

  @Builder
  @Getter
  public static class ChannelPattern {
    @Singular("assign")
    private Map<DiscordGroup, ChannelRolePattern> data;
  }
}
