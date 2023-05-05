package de.zahrie.trues.api.discord.group;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.discord.util.Nunu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;

@RequiredArgsConstructor
@Getter
@Listing(Listing.ListingType.UPPER)
public enum DiscordGroup implements Roleable {
  EVERYONE(0, "@everyone", 1094596402098733129L, GroupTier.EVERYONE, Department.NONE, GroupType.DEFAULT, Set.of()),
  REGISTERED(1, "Bestätigt", 1094596402098733134L, GroupTier.REGISTERED, Department.NONE, GroupType.PINGABLE, Set.of()),
  APPLICANT(2, "Bewerber", 1094596402581078049L, GroupTier.APPLICANT, Department.NONE, GroupType.PINGABLE, Set.of()),
  TRYOUT(3, "Tryout", 1094596402581078050L, GroupTier.TRYOUT, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUBSTITUDE(4, "Substitude", 1094596402581078051L, GroupTier.SUBSTITUDE, Department.NONE, GroupType.PINGABLE, Set.of()),
  EVENT_PLANNING(5, "Eventplanung", 1094596402614653066L, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  CASTER(6, "Cast", 1094596402597863479L, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  DEVELOPER(7, "Developer", 1094596402581078053L, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  SOCIAL_MEDIA(8, "Social Media", 1094596402597863478L, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  PLAYER(9, "Spieler", 1094596402581078052L, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  MENTOR(10, "Mentor", 1094596402597863477L, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  ANALYST(11, "Analyst", 1094596402597863476L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  DRAFT_COACH(12, "Draft Coach", 1094596402597863475L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  LANE_COACH(13, "Lane Coach", 1094596402597863474L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  MENTAL_COACH(14, "Mental Coach", 1094596402581078055L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  STRATEGIC_COACH(15, "Strategic Coach", 1094596402581078054L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  EVENT_LEAD(16, "Head of Event", 1094596402635603988L, GroupTier.LEADER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  SOCIAL_MEDIA_LEAD(17, "Head of Social Media", 1094596402614653069L, GroupTier.LEADER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  TEAM_CAPTAIN(18, "Captain", 1094596402614653067L, GroupTier.LEADER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  TEAM_BUILDING(19, "Teamaufbau", 1094596402614653068L, GroupTier.LEADER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  EVENT_MANAGER(20, "Event Management", 1094596402635603989L, GroupTier.MANAGEMENT, Department.EVENT, GroupType.PINGABLE, Set.of()),
  COMMUNITY_MANAGER(21, "Community Management", 1094596402635603991L, GroupTier.MANAGEMENT, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  COACHING_MANAGER(22, "Team Management", 1094596402635603990L, GroupTier.MANAGEMENT, Department.COACHING, GroupType.PINGABLE, Set.of()),
  ORGA_LEADER(23, "Leitung Community", 1094596402656583770L, GroupTier.ORGA_LEADER, Department.ALL, GroupType.PINGABLE, Set.of()),
  TEAM_ROLE_PLACEHOLDER(24, "", -1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  TOP(50, "Top", 1094596402560127063L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  JUNGLE(51, "Jungle", 1094596402560127062L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  MIDDLE(52, "Middle", 1094596402560127061L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  BOTTOM(53, "Bottom", 1094596402560127060L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUPPORT(54, "Support", 1094596402560127059L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  TOP_HELP(55, "Top-Selbsthilfe", 1094596402560127058L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  JUNGLE_HELP(56, "Jungle-Selbsthilfe", 1094596402560127057L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  MIDDLE_HELP(57, "Middle-Selbsthilfe", 1094596402534948873L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  BOTTOM_HELP(58, "Bottom-Selbsthilfe", 1094596402534948872L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUPPORT_HELP(59, "Support-Selbsthilfe", 1094596402534948871L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),


  ORGA_MEMBER(101, "Ein TRUE", 1094596402635603995L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.ORGA_MEMBER, Set.of()),
  STAFF(102, "Staff", 1094596402635603996L, GroupTier.LEADER, Department.NONE, GroupType.STAFF, Set.of()),
  ADMIN(103, "Admin", 1094596402635603997L, GroupTier.MANAGEMENT, Department.NONE, GroupType.ADMIN, Set.of()),


  EVENT(201, "Event&Content", 1094596402635603992L, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.CONTENT, Set.of()),
  TEAMS(202, "Teams", 1094596402635603993L, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  COACHING(203, "Teamentwicklung", 1094596402635603994L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),


  VIP(1000, "VIP", 1094596402656583771L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  FRIEND(1001, "Family & Friends", 1094596402581078048L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  SCRIMPARTNER(1002, "Scrimpartner", 1094596402581078047L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of());

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
