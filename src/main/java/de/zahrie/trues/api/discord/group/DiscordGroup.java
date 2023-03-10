package de.zahrie.trues.api.discord.group;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.discord.Nunu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;

@RequiredArgsConstructor
@Getter
public enum DiscordGroup implements Roleable {
  EVERYONE(0, "@everyone", 1, GroupTier.EVERYONE, Department.NONE, GroupPattern.DEFAULT, Set.of()),
  REGISTERED(1, "Bestätigt", 1, GroupTier.REGISTERED, Department.NONE, GroupPattern.PINGABLE, Set.of()),
  APPLICANT(2, "Bewerber", 1, GroupTier.APPLICANT, Department.NONE, GroupPattern.PINGABLE, Set.of()),
  TRYOUT(3, "Tryout", 1, GroupTier.TRYOUT, Department.NONE, GroupPattern.PINGABLE, Set.of()),
  ACCEPTED(4, "Stand-in", 1, GroupTier.ACCEPTED, Department.NONE, GroupPattern.PINGABLE, Set.of()),
  EVENT_PLANNING(5, "Eventplanung", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupPattern.PINGABLE, Set.of()),
  CASTER(6, "Cast", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupPattern.PINGABLE, Set.of()),
  DEVELOPER(7, "Developer", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupPattern.PINGABLE, Set.of()),
  SOCIAL_MEDIA(8, "Social Media", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupPattern.PINGABLE, Set.of()),
  PLAYER(9, "Spieler", 1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupPattern.PINGABLE, Set.of()),
  MENTOR(10, "Mentor", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupPattern.PINGABLE, Set.of()),
  ANALYST(11, "Analyst", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupPattern.PINGABLE, Set.of()),
  LANE_COACH(12, "Lane Coach", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupPattern.PINGABLE, Set.of()),
  MENTAL_COACH(13, "Mental Coach", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupPattern.PINGABLE, Set.of()),
  STRATEGIC_COACH(14, "Strategic Coach", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupPattern.PINGABLE, Set.of()),
  EVENT_LEAD(15, "Head of Event", 1, GroupTier.LEADER, Department.EVENT, GroupPattern.PINGABLE, Set.of()),
  SOCIAL_MEDIA_LEAD(16, "Head of Social Media", 1, GroupTier.LEADER, Department.EVENT, GroupPattern.PINGABLE, Set.of()),
  TEAM_CAPTAIN(17, "Captain", 1, GroupTier.LEADER, Department.TEAMS, GroupPattern.PINGABLE, Set.of()),
  COACH_LEAD(18, "Headcoach", 1, GroupTier.LEADER, Department.COACHING, GroupPattern.PINGABLE, Set.of()),
  EVENT_MANAGER(19, "Event Management", 1, GroupTier.MANAGEMENT, Department.EVENT, GroupPattern.PINGABLE, Set.of()),
  COMMUNITY_MANAGER(20, "Community Management", 1, GroupTier.MANAGEMENT, Department.TEAMS, GroupPattern.PINGABLE, Set.of()),
  COACHING_MANAGER(21, "Team Management", 1, GroupTier.MANAGEMENT, Department.COACHING, GroupPattern.PINGABLE, Set.of()),
  ORGA_LEADER(22, "Leitung Community", 1, GroupTier.ORGA_LEADER, Department.ALL, GroupPattern.PINGABLE, Set.of()),


  ORGA_MEMBER(101, "TRUE", 1, GroupTier.ORGA_MEMBER, Department.NONE, GroupPattern.ORGA_MEMBER, Set.of()),
  STAFF(102, "Staff", 1, GroupTier.LEADER, Department.NONE, GroupPattern.STAFF, Set.of()),
  ADMIN(103, "Admin", 1, GroupTier.MANAGEMENT, Department.NONE, GroupPattern.ADMIN, Set.of()),


  EVENT(201, "Event", 1, GroupTier.ORGA_MEMBER, Department.EVENT, GroupPattern.CONTENT, Set.of()),
  TEAMS(202, "Teams", 1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupPattern.PINGABLE, Set.of()),
  COACHING(203, "Coaching", 1, GroupTier.ORGA_MEMBER, Department.COACHING, GroupPattern.PINGABLE, Set.of()),


  VIP(1000, "VIP", 1, GroupTier.EVERYONE, Department.NONE, GroupPattern.PINGABLE, Set.of()),
  FRIEND(1001, "Family & Friends", 1, GroupTier.EVERYONE, Department.NONE, GroupPattern.PINGABLE, Set.of()),
  SCRIMPARTNER(1002, "Scrimpartner", 1, GroupTier.EVERYONE, Department.NONE, GroupPattern.PINGABLE, Set.of());

  private final int id;
  private final String name;
  private final long discordId;
  private final GroupTier tier;
  private final Department department;
  private final GroupPattern pattern;  
  private final Set<DiscordGroup> assignable;

  public Role getRole() {
    return Nunu.guild.getRoleById(discordId);
  }


  public static DiscordGroup of(int id) {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(group -> group.getId() == id)
        .findFirst().orElse(null);
  }

  public static DiscordGroup of(long id) {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(group -> group.getDiscordId() == id)
        .findFirst().orElse(null);
  }

  public static DiscordGroup of(Chain name) {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(group -> name.equalsCase(group.getName()))
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
}
