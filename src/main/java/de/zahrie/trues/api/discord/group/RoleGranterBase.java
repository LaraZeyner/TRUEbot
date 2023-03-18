package de.zahrie.trues.api.discord.group;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.api.discord.member.DiscordMemberGroup;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.discord.Nunu;
import de.zahrie.trues.models.community.OrgaTeam;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;

@Getter
public class RoleGranterBase {
  protected final DiscordMember target;
  protected final DiscordMember invoker;

  public RoleGranterBase(DiscordMember target) {
    this(null, target);
  }

  public RoleGranterBase(DiscordMember target, DiscordMember invoker) {
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

  public void perform(DiscordGroup group) {
    if (target.getMember().getRoles().contains(group.getRole())) {
      target.removeGroup(group, true);
    } else {
      target.addGroup(group, true);
    }
  }


  public void add(DiscordGroup group) {
    add(group, true, new Time(), 0);
  }

  public void add(DiscordGroup group, int days) {
    add(group, true, new Time(), days);
  }

  public void add(DiscordGroup group, boolean perform, Time start, int days) {
    if (days > 0) {
      final var memberGroup = new DiscordMemberGroup(target, group, start, start.plus(Time.DATE, days));
      target.getGroups().add(memberGroup);
      Database.save(memberGroup);
      Database.save(this);
    }
    target.getGroups().stream().filter(DiscordMemberGroup::isActive)
        .filter(discordMemberGroup -> discordMemberGroup.getDiscordGroup().equals(group)).findFirst().ifPresent(discordMemberGroup -> {
          if (days > 0) {
            final Time end = discordMemberGroup.getPermissionEnd();
            final Time newEnd = start.plus(Time.DATE, days);
            if (newEnd.after(end)) {
              discordMemberGroup.setPermissionEnd(newEnd);
            }
          } else {
            discordMemberGroup.setActive(false);
            discordMemberGroup.setPermissionEnd(new Time());
          }
          Database.save(discordMemberGroup);
        });

    if (perform) {
      final DiscordGroup departmentGroup = group.getDepartment().getGroup();
      if (departmentGroup != null) {
        addRole(departmentGroup.getRole());
      }
      for (final DiscordGroup pingableGroup : group.getTier().getPingableGroups()) {
        if (pingableGroup != null) {
          addRole(pingableGroup.getRole());
        }
      }
      addRole(group.getRole());
    }
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

  public void remove(DiscordGroup group) {
    remove(group, true);
  }

  public void remove(DiscordGroup group, boolean perform) {
    if (perform) {
      if (!target.getMember().getRoles().contains(group.getRole())) {
        return;
      }
      final var groups = new HashSet<>(target.getActiveGroups());
      groups.remove(group);
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
      removeRole(group.getRole());
    }
  }

  public void addTeam(OrgaTeam team) {
    addRole(team.getRole());
  }

  public void removeTeam(OrgaTeam team) {
    removeRole(team.getRole());
  }

}
