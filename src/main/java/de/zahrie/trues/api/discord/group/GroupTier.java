package de.zahrie.trues.api.discord.group;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GroupTier {
  EVERYONE(10, Set.of()),
  REGISTERED(20, Set.of()),
  APPLICANT(30, Set.of()),
  TRYOUT(40, Set.of()),
  SUBSTITUDE(50, Set.of()),
  ORGA_MEMBER(60, Set.of(DiscordGroup.FRIEND)),
  LEADER(70, Set.of(DiscordGroup.FRIEND, DiscordGroup.SCRIMPARTNER)),
  MANAGEMENT(80, Set.of(DiscordGroup.FRIEND, DiscordGroup.SCRIMPARTNER, DiscordGroup.MENTOR, DiscordGroup.ANALYST,
      DiscordGroup.LANE_COACH, DiscordGroup.MENTAL_COACH, DiscordGroup.STRATEGIC_COACH)),
  ORGA_LEADER(90, Set.of(DiscordGroup.FRIEND, DiscordGroup.SCRIMPARTNER, DiscordGroup.MENTOR, DiscordGroup.ANALYST,
      DiscordGroup.LANE_COACH, DiscordGroup.MENTAL_COACH, DiscordGroup.STRATEGIC_COACH));

  private final int permissionId;
  private final Set<DiscordGroup> assignable;

  public boolean isOrga() {
    return permissionId >= SUBSTITUDE.getPermissionId();
  }

  public boolean isStaff() {
    return permissionId >= LEADER.getPermissionId();
  }

  public boolean isAdmin() {
    return permissionId >= MANAGEMENT.getPermissionId();
  }

  public Set<DiscordGroup> getPingableGroups() {
    final var groups = new HashSet<DiscordGroup>();
    if (isOrga()) {
      groups.add(DiscordGroup.ORGA_MEMBER);
      if (isStaff()) {
        groups.add(DiscordGroup.STAFF);
        if (isAdmin()) {
          groups.add(DiscordGroup.ADMIN);
        }
      }
    }
    return groups;
  }

  public Set<DiscordGroup> getGroups() {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(group -> group.getTier().equals(this))
        .collect(Collectors.toSet());
  }

  public Set<DiscordGroup> requires() {
    final Stream<DiscordGroup> groups = Arrays.stream(DiscordGroup.values());
    return groups.filter(this::isNotBelow).collect(Collectors.toSet());
  }

  private boolean isNotBelow(DiscordGroup group) {
    return group.getTier().getPermissionId() >= permissionId;
  }

  public DiscordGroup getGroup() {
    return DiscordGroup.valueOf(name());
  }
}
