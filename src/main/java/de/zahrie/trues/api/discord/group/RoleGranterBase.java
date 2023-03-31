package de.zahrie.trues.api.discord.group;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserGroup;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;

@Getter
public class RoleGranterBase {
  protected final DiscordUser target;
  protected final DiscordUser invoker;

  public RoleGranterBase(DiscordUser target) {
    this(null, target);
  }

  public RoleGranterBase(DiscordUser target, DiscordUser invoker) {
    this.target = target;
    this.invoker = invoker;
  }

  public boolean isNotEmpty() {
    return getAssignGroups().size() + getRemoveGroups().size() > 0;
  }

  public Set<DiscordGroup> getAssignGroups() {
    final Set<DiscordGroup> assignable = getGroups();
    assignable.removeAll(target.getActiveGroups());
    return assignable;
  }

  public Set<DiscordGroup> getRemoveGroups() {
    final Set<DiscordGroup> assignable = getGroups();
    assignable.retainAll(target.getActiveGroups());
    return assignable;
  }

  public Set<DiscordGroup> getGroups() {
    final Set<DiscordGroup> activeGroups = invoker.getActiveGroups();
    return activeGroups.stream().flatMap(activeGroup -> activeGroup.getAssignable().stream()).collect(Collectors.toSet());
  }

  public Set<DiscordGroup> getMemberGroups() {
    final Set<DiscordGroup> activeGroups = invoker.getActiveGroups();
    if (activeGroups.stream().anyMatch(activeGroup -> activeGroup.getTier().isAdmin())) {
      return GroupTier.ORGA_MEMBER.getGroups().stream().filter(group -> !group.getDepartment().equals(Department.TEAMS)).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public void perform(DiscordGroup group) {
    if (target.getMember().getRoles().contains(group.getRole())) {
      target.removeGroup(group);
    } else {
      target.addGroup(group);
    }
  }


  public RoleGranterBase add(DiscordGroup group) {
    return add(group, new Time(), 0);
  }

  public RoleGranterBase add(DiscordGroup group, int days) {
    return add(group, new Time(), days);
  }

  public void updateRelatedRoles(DiscordUser user) {
    //TODO (Abgie) 21.03.2023: never used
    final Set<DiscordGroup> departmentGroups = user.getActiveGroups().stream().map(group -> group.getDepartment().getGroup()).filter(Objects::nonNull).collect(Collectors.toSet());
    for ( DiscordGroup pingableGroup : Department.ALL.getPingableGroups()) {
      if (departmentGroups.contains(pingableGroup)) {
        addRole(pingableGroup.getRole());
      } else {
        removeRole(pingableGroup.getRole());
      }
    }

    final Set<DiscordGroup> orgaGroups = user.getActiveGroups().stream().flatMap(group -> group.getTier().getInheritedGroups().stream()).collect(Collectors.toSet());
    for ( DiscordGroup group : List.of(DiscordGroup.ORGA_MEMBER, DiscordGroup.STAFF, DiscordGroup.ADMIN)) {
      if (orgaGroups.contains(group)) {
        addRole(group.getRole());
      } else {
        removeRole(group.getRole());
      }
    }
  }

  public RoleGranterBase add(DiscordGroup group, Time start, int days) {
    if (days > 0) {
      final var userGroup = new DiscordUserGroup(target, group, start, start.plus(Time.DATE, days));
      target.getGroups().add(userGroup);
      Database.save(userGroup);
      Database.save(this);
    }
    target.getGroups().stream().filter(DiscordUserGroup::isActive)
        .filter(discordUserGroup -> discordUserGroup.getDiscordGroup().equals(group)).findFirst().ifPresent(discordUserGroup -> {
          if (days > 0) {
            final Time end = discordUserGroup.getPermissionEnd();
            final Time newEnd = start.plus(Time.DATE, days);
            if (newEnd.after(end)) {
              discordUserGroup.setPermissionEnd(newEnd);
            }
          } else {
            discordUserGroup.setActive(false);
            discordUserGroup.setPermissionEnd(new Time());
          }
          Database.save(discordUserGroup);
        });

    updateRelatedRolesOnAdd(group);
    addRole(group.getRole());
    return this;
  }

  private void updateRelatedRolesOnAdd(DiscordGroup group) {
    final DiscordGroup departmentGroup = group.getDepartment().getGroup();
    if (departmentGroup != null) addRole(departmentGroup.getRole());
    group.getTier().getInheritedGroups().stream().filter(Objects::nonNull).map(DiscordGroup::getRole).forEach(this::addRole);
  }

  /**
   * @param role Rolle wird nicht durch DiscordGroup repräsentiert
   */
  private void removeRole(Role role) {
    Nunu.DiscordRole.removeRole(target.getMember(), role);
  }

  /**
   * @param role Rolle wird nicht durch DiscordGroup repräsentiert
   */
  private void addRole(Role role) {
    Nunu.DiscordRole.addRole(target.getMember(), role);
  }

  public RoleGranterBase remove(DiscordGroup group) {
    if (!target.getMember().getRoles().contains(group.getRole())) {
      return this;
    }
    updateRelatedRolesOnRemove(group);
    removeRole(group.getRole());
    return this;
  }

  private void updateRelatedRolesOnRemove(DiscordGroup group) {
    final var groups = new HashSet<>(target.getActiveGroups());
    if (!groups.remove(group)) {
      return;
    }
    if (groups.stream().map(DiscordGroup::getDepartment).noneMatch(department -> department.equals(group.getDepartment()))) {
      final DiscordGroup departmentGroup = group.getDepartment().getGroup();
      if (departmentGroup != null && !departmentGroup.equals(group)) {
        removeRole(departmentGroup.getRole());
      }
    }

    if (groups.stream().map(DiscordGroup::getTier).noneMatch(tier -> tier.equals(group.getTier()))) {
      final DiscordGroup tierGroup = group.getTier().getGroup();
      if (tierGroup != null && !tierGroup.equals(group)) {
        removeRole(tierGroup.getRole());
      }
    }
  }

  public void addTeam(OrgaTeam team) {
    addRole(team.getGroup().getRole());
  }

  public void addOrga(TeamPosition position) {
    if (position.getDiscordGroup() == null) throw new IllegalArgumentException("Kein Orgamember");
    addRole(position.getDiscordGroup().getRole());
  }

  public void removeTeam(OrgaTeam team) {
    removeRole(team.getGroup().getRole());
  }

  public void removeOrga(TeamPosition position) {
    if (position.getDiscordGroup() == null) throw new IllegalArgumentException("Kein Orgamember");
    removeRole(position.getDiscordGroup().getRole());
  }

}
