package de.zahrie.trues.api.discord.group;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PermissionRole {
  public static final Integer NO_ROLES = null;
  public static final Integer EVERYONE = 0;
  public static final Integer REGISTERED = 1;
  public static final Integer APPLICANT = 2;
  public static final Integer TRYOUT = 3;
  public static final Integer ACCEPTED = 4;
  public static final Integer EVENT_PLANNING = 5;
  public static final Integer CASTER = 6;
  public static final Integer SOCIAL_MEDIA = 7;
  public static final Integer PLAYER = 8;
  public static final Integer COACH = 9;
  public static final Integer EVENT_LEAD = 10;
  public static final Integer SOCIAL_MEDIA_LEAD = 11;
  public static final Integer TEAM_CAPTAIN = 12;
  public static final Integer COACH_LEAD = 13;
  public static final Integer EVENT_MANAGER = 14;
  public static final Integer COMMUNITY_MANAGER = 15;
  public static final Integer COACHING_MANAGER = 16;
  public static final Integer ORGA_LEADER = 17;


  public static final Integer ORGA_MEMBER = 105;
  public static final Integer LEADER = 106;
  public static final Integer MANAGEMENT = 107;

  public static final Integer NOT_IN_ORGA = 200;
  public static final Integer EVENT = 201;
  public static final Integer TEAMS = 202;
  public static final Integer COACHING = 203;
  public static final Integer ALL_DEPARTMENTS = 204;

  public static Set<DiscordGroup> of(Integer id, boolean recursive) {
    if (!recursive || id >= 200) {
      return of(id);
    }
    if (id < 100) {
      final DiscordGroup groupFound = DiscordGroup.of(id);
      final Set<DiscordGroup> groups = new HashSet<>(Set.of(groupFound));
      groups.addAll(groupFound.requires());
      return groups;
    }
    final GroupTier tier = GroupTier.values()[id - 100];
    return tier.requires();
  }

  private static Set<DiscordGroup> of(Integer id) {
    if (id == null) {
      return Set.of();
    }
    if (id >= 1000) {
      return Set.of(DiscordGroup.of(id));
    }
    if (id >= 200) {
      final Department department = Department.values()[id - 200];
      return department.getGroups();
    }
    if (id >= 100) {
      final GroupTier tier = GroupTier.values()[id - 100];
      return tier.getGroups();
    }

    return Set.of(DiscordGroup.of(id));
  }
}
