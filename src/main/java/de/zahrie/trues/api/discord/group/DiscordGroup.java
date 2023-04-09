package de.zahrie.trues.api.discord.group;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.zahrie.trues.api.discord.util.Nunu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;

@RequiredArgsConstructor
@Getter
public enum DiscordGroup implements Roleable {
  EVERYONE(0, "@everyone", 1, GroupTier.EVERYONE, Department.NONE, GroupType.DEFAULT, Set.of()),
  REGISTERED(1, "Bestätigt", 1, GroupTier.REGISTERED, Department.NONE, GroupType.PINGABLE, Set.of()),
  APPLICANT(2, "Bewerber", 1, GroupTier.APPLICANT, Department.NONE, GroupType.PINGABLE, Set.of()),
  TRYOUT(3, "Tryout", 1, GroupTier.TRYOUT, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUBSTITUDE(4, "Substitude", 1, GroupTier.SUBSTITUDE, Department.NONE, GroupType.PINGABLE, Set.of()),
  EVENT_PLANNING(5, "Eventplanung", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  CASTER(6, "Cast", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  DEVELOPER(7, "Developer", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  SOCIAL_MEDIA(8, "Social Media", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  PLAYER(9, "Spieler", 1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  MENTOR(10, "Mentor", 1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  ANALYST(11, "Analyst", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  DRAFT_COACH(12, "Draft Coach", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  LANE_COACH(13, "Lane Coach", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  MENTAL_COACH(14, "Mental Coach", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  STRATEGIC_COACH(15, "Strategic Coach", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  EVENT_LEAD(16, "Head of Event", 1, GroupTier.LEADER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  SOCIAL_MEDIA_LEAD(17, "Head of Social Media", 1, GroupTier.LEADER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  TEAM_CAPTAIN(18, "Captain", 1, GroupTier.LEADER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  TEAM_BUILDING(19, "Teamaufbau", 1, GroupTier.LEADER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  EVENT_MANAGER(20, "Event Management", 1, GroupTier.MANAGEMENT, Department.EVENT, GroupType.PINGABLE, Set.of()),
  COMMUNITY_MANAGER(21, "Community Management", 1, GroupTier.MANAGEMENT, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  COACHING_MANAGER(22, "Team Management", 1, GroupTier.MANAGEMENT, Department.COACHING, GroupType.PINGABLE, Set.of()),
  ORGA_LEADER(23, "Leitung Community", 1, GroupTier.ORGA_LEADER, Department.ALL, GroupType.PINGABLE, Set.of()),
  TEAM_ROLE_PLACEHOLDER(24, "", -1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  TOP(50, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  JUNGLE(51, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  MIDDLE(52, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  BOTTOM(53, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUPPORT(54, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  TOP_HELP(55, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  JUNGLE_HELP(56, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  MIDDLE_HELP(57, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  BOTTOM_HELP(58, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUPPORT_HELP(59, "", -1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),


  ORGA_MEMBER(101, "TRUE", 1, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.ORGA_MEMBER, Set.of()),
  STAFF(102, "Staff", 1, GroupTier.LEADER, Department.NONE, GroupType.STAFF, Set.of()),
  ADMIN(103, "Admin", 1, GroupTier.MANAGEMENT, Department.NONE, GroupType.ADMIN, Set.of()),


  EVENT(201, "Event", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.CONTENT, Set.of()),
  TEAMS(202, "Teams", 1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  COACHING(203, "Coaching", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),


  VIP(1000, "VIP", 1, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  FRIEND(1001, "Family & Friends", 1, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  SCRIMPARTNER(1002, "Scrimpartner", 1, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of());

  private final int id;
  private final String name;
  private final long discordId;
  private final GroupTier tier;
  private final Department department;
  private final GroupType pattern;  
  private final Set<DiscordGroup> assignable;

  public Role getRole() {
    return Nunu.DiscordRole.getRole(discordId);
  }

  public static DiscordGroup of(int id) {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(group -> group.getId() == id)
        .findFirst().orElse(null);
  }

  public static DiscordGroup of(Role role) {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(group -> group.getDiscordId() == role.getIdLong())
        .findFirst().orElse(null);
  }

  public static DiscordGroup of(String name) {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(group -> name.equalsIgnoreCase(group.getName()))
        .findFirst().orElse(null);
  }

  public Set<DiscordGroup> requires() {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(this::isAbove).collect(Collectors.toSet());
  }

  public boolean isAbove(DiscordGroup discordGroup) {
    return discordGroup.getTier().getPermissionId() < tier.getPermissionId();
  }

  public Set<DiscordGroup> getAssignable() {
    final HashSet<DiscordGroup> assignGroups = new HashSet<>(assignable);
    assignGroups.addAll(tier.getAssignable());
    return assignGroups;
  }


  public void updatePermissions() {
    if (getRole() != null) {
      getRole().getManager().setPermissions(pattern.getPattern().getAllowed()).queue();
    }
  }
}
