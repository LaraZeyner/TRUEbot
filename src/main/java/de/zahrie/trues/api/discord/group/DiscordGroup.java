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
  EVERYONE(0, "@everyone", 540216137628254223L, GroupTier.EVERYONE, Department.NONE, GroupType.DEFAULT, Set.of()),
  REGISTERED(1, "Bestätigt", 1031835432691961866L, GroupTier.REGISTERED, Department.NONE, GroupType.PINGABLE, Set.of()),
  APPLICANT(2, "Bewerber", 1028923616290676776L, GroupTier.APPLICANT, Department.NONE, GroupType.PINGABLE, Set.of()),
  TRYOUT(3, "Tryout", 1031833582932611072L, GroupTier.TRYOUT, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUBSTITUTE(4, "Substitude", 1049255537960824902L, GroupTier.SUBSTITUDE, Department.NONE, GroupType.PINGABLE, Set.of()),
  EVENT_PLANNING(5, "Eventplanung", 1110091981536903218L, GroupTier.LEADER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  CASTER(6, "Caster", 1035210811611820053L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.PINGABLE, Set.of()),
  DEVELOPER(7, "Developer", 1024587566198042666L, GroupTier.LEADER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  SOCIAL_MEDIA(8, "Social Media", 1035215111922655292L, GroupTier.LEADER, Department.EVENT, GroupType.PINGABLE, Set.of()),
  PLAYER(9, "Spieler", 1013025140222726164L, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  ANALYST(11, "Analyst", 1035214858402140231L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  LANE_COACH(13, "Individueller Coach", 1035214651669102612L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  MENTAL_COACH(14, "Mental Coach", 1035214604814520401L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  TEAM_COACH(15, "Team Coach", 1035214448996130886L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),
  TEAM_CAPTAIN(18, "Teamcaptain", 1030764031423815760L, GroupTier.LEADER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  TEAM_MANAGER(19, "Teamaufbau", 1094596402614653068L, GroupTier.MANAGEMENT, Department.COACHING, GroupType.PINGABLE, Set.of()),
  EVENT_MANAGER(20, "Event Management", 1080159663036841995L, GroupTier.MANAGEMENT, Department.EVENT, GroupType.PINGABLE, Set.of()),
  ORGA_LEADER(23, "Leitung Community", 1084395080472866846L, GroupTier.ORGA_LEADER, Department.ALL, GroupType.PINGABLE, Set.of()),
  TEAM_ROLE_PLACEHOLDER(24, "", -1, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  TOP(50, "Top", 1004701482626580630L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  JUNGLE(51, "Jungle", 1004701701284053143L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  MIDDLE(52, "Middle", 1004701741700358284L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  BOTTOM(53, "Bottom", 1004701775254790215L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  SUPPORT(54, "Support", 1004701819605368894L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  HELP(55, "Selbsthilfe", 1072087015782371328L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),


  ORGA_MEMBER(101, "Ein TRUE", 1035215421625864273L, GroupTier.ORGA_MEMBER, Department.NONE, GroupType.ORGA_MEMBER, Set.of()),
  STAFF(102, "Staff", 655022311123976192L, GroupTier.LEADER, Department.NONE, GroupType.STAFF, Set.of()),
  ADMIN(103, "Admin", 924432953440886854L, GroupTier.MANAGEMENT, Department.NONE, GroupType.ADMIN, Set.of()),


  EVENT(201, "Event&Content", 1035195791767240746L, GroupTier.ORGA_MEMBER, Department.EVENT, GroupType.CONTENT, Set.of()),
  TEAMS(202, "Teams", 1086230822580912169L, GroupTier.ORGA_MEMBER, Department.TEAMS, GroupType.PINGABLE, Set.of()),
  COACHING(203, "Teamentwicklung", 1086231152458727474L, GroupTier.ORGA_MEMBER, Department.COACHING, GroupType.PINGABLE, Set.of()),


  VIP(1000, "VIP", 974340225763516476L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  FRIEND(1001, "Family&Friends", 972956350558662726L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of()),
  SCRIMPARTNER(1002, "Scrimpartner", 974004880761053314L, GroupTier.EVERYONE, Department.NONE, GroupType.PINGABLE, Set.of());

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
    if (discordGroup == null) return false;
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
