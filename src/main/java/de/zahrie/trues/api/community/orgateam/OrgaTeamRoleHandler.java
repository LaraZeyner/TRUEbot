package de.zahrie.trues.api.community.orgateam;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import de.zahrie.trues.api.discord.group.DiscordRoleFactory;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.util.Util;
import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.Role;

@AllArgsConstructor
@ExtensionMethod(MembershipFactory.class)
public class OrgaTeamRoleHandler {
  private OrgaTeam team;

  private void refresh() {
    this.team = QueryBuilder.hql(OrgaTeam.class, "FROM OrgaTeam WHERE id = " + team.getId()).single();
  }

  public void addRole(DiscordUser user, TeamRole role, TeamPosition position) {
    if (role.equals(TeamRole.MAIN)) checkMainOnPosition(position);
    final Membership member = team.getMember(user, role, position);
    new RoleGranter(member.getUser()).addTeamRole(role, position, team);
  }
  private void checkMainOnPosition(TeamPosition position) {
    final Membership mainOnPosition = team.getOfTeam(TeamRole.MAIN, position);
    if (mainOnPosition != null) {
      mainOnPosition.setRole(TeamRole.SUBSTITUDE);
      Database.update(mainOnPosition);
    }
  }

  public void addCaptain(DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    membership.setCaptain(true);
    Database.update(membership);
    new RoleGranter(user).handleCaptain(true);
  }

  public void removeCaptain(DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    membership.setCaptain(false);
    Database.update(membership);
    new RoleGranter(user).handleCaptain(false);
  }

  public void removeRole(DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    if (membership == null) return;
    membership.setActive(false);
    Database.update(membership);
    new RoleGranter(user).removeTeamRole(membership, team);
  }

  public CustomDiscordGroup getGroup() {
    try {
      return team.getGroup();
    } catch (IllegalStateException ignored) {
      return QueryBuilder.hql(CustomDiscordGroup.class, "FROM CustomDiscordGroup WHERE team = :team").addParameter("team", team).single();
    }
  }

  public Role getRole() {
    try {
      final Role role = getGroup().determineRole();
      return Util.nonNull(role, "Fehler bei der Teamerstellung");
    } catch (IllegalStateException exception) {
      refresh();
      return getRole();
    }
  }

  public String getRoleName() {
    return "TRUE " + roleNameFromString(team.getName());
  }

  private String roleNameFromString(String name) {
    return "TRUE " + name.replace("Technical Really Unique ", "")
        .replace("Technical Really ", "")
        .replace("TRUEsports ", "")
        .replace("TRUE ", "");
  }

  public void updateRoleName(String newName) {
    final Role role = getRole();
    role.getManager().setName(newName).queue();
    final CustomDiscordGroup customGroup = DiscordRoleFactory.getCustomGroup(role);
    if (customGroup != null) {
      customGroup.setName(newName);
      Database.update(customGroup);
    }
  }
}
