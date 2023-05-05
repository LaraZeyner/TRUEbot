package de.zahrie.trues.api.community.orgateam;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
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

  public void addRole(DiscordUser user, TeamRole role, TeamPosition position) {
    if (role.equals(TeamRole.MAIN)) checkMainOnPosition(position);
    final Membership member = team.getMember(user, role, position);
    new RoleGranter(member.getUser()).addTeamRole(role, position, team);
  }
  private void checkMainOnPosition(TeamPosition position) {
    final Membership mainOnPosition = team.getMembership(TeamRole.MAIN, position);
    if (mainOnPosition != null) mainOnPosition.setRole(TeamRole.SUBSTITUTE);
  }

  public void addCaptain(DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    membership.setCaptain(true);
  }

  public void removeCaptain(DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    membership.setCaptain(false);
  }

  public void removeRole(DiscordUser user) {
    final Membership membership = MembershipFactory.getMembershipOf(user, team);
    if (membership == null) return;
    membership.removeFromTeam(team);
  }

  public Role getRole() {
    final Role role = team.getGroup().determineRole();
    return Util.nonNull(role, "Fehler bei der Teamerstellung");
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
  }
}
