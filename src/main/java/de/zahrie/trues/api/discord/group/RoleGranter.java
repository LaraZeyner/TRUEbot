package de.zahrie.trues.api.discord.group;

import de.zahrie.trues.api.community.application.ApplicationFactory;
import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.logging.TeamLog;
import de.zahrie.trues.api.logging.TeamLogFactory;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.emoji.Emoji;

@ExtensionMethod(ApplicationFactory.class)
public class RoleGranter extends RoleGranterBase {
  public RoleGranter(DiscordUser target) {
    super(target);
  }

  public RoleGranter(DiscordUser target, DiscordUser invoker) {
    super(target, invoker);
  }

  public void addTeamRole(TeamRole role, TeamPosition position, @NonNull OrgaTeam team) {
    TeamLogFactory.create(invoker, target, target.getMention() + " ist neuer " +
        Util.avoidNull(position.getEmoji(), position.getName(), Emoji::getFormatted) + " (" + role.getName() +
        ") bei **" + team.getName() + "**", TeamLog.TeamLogAction.LINEUP_JOIN, team);
    addTeam(team);
    if (role.equals(TeamRole.TRYOUT)) target.take(role, position);
    if (role.equals(TeamRole.STANDIN)) new RoleGranter(target).add(DiscordGroup.SUBSTITUDE, 1);
    target.updateApplicationStatus();
    updateBasedOnGivenRolesAndMembers();
  }

  public void addOrgaRole(TeamRole role, TeamPosition position) {
    addOrga(position);
    if (role.equals(TeamRole.ORGA_TRYOUT)) add(DiscordGroup.SUBSTITUDE, 14);
    target.updateApplicationStatus();
  }

  public void removeTeamRole(Membership member, @NonNull OrgaTeam team) {
    TeamLogFactory.create(invoker, target, target.getMention() + " verlässt " +
        team.getTeam().getName(), TeamLog.TeamLogAction.LINEUP_LEAVE, team);
    removeTeam(team);
    target.demote(member.getRole(), member.getPosition());
    updateBasedOnGivenRolesAndMembers();
  }

  public void removeOrgaRole(Membership member) {
    removeOrga(member.getPosition());
    target.demote(member.getRole(), member.getPosition());
    updateBasedOnGivenRolesAndMembers();
  }


  private void updateBasedOnGivenRolesAndMembers() {
    target.updateTeamRoleRole();
    target.updateOrgaTeamRole();
  }

  public void handleCaptain(boolean b) {
    if (b) add(DiscordGroup.TEAM_CAPTAIN);
    else target.updateTeamRoleRole();
  }
}
