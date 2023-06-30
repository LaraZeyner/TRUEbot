package de.zahrie.trues.api.discord.group;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.logging.TeamLog;
import de.zahrie.trues.api.logging.TeamLogFactory;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.emoji.Emoji;

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
    if (role.equals(TeamRole.TRYOUT)) target.getApplications().take(role, position);
    if (role.equals(TeamRole.STANDIN)) new RoleGranter(target).add(DiscordGroup.SUBSTITUTE, 1);
    target.getApplications().updateApplicationStatus();
    updateBasedOnGivenRolesAndMembers();
  }

  public void addOrgaRole(TeamRole role, TeamPosition position) {
    addOrga(position);
    if (role.equals(TeamRole.ORGA_TRYOUT)) add(DiscordGroup.SUBSTITUTE, 14);
    target.getApplications().updateApplicationStatus();
  }

  public void removeTeamRole(Membership member, @NonNull OrgaTeam team) {
    TeamLogFactory.create(invoker, target, target.getMention() + " verlässt " + team.getName(), TeamLog.TeamLogAction.LINEUP_LEAVE, team);
    removeTeam(team);
    target.getApplications().demote(member.getRole(), member.getPosition());
    updateBasedOnGivenRolesAndMembers();
  }

  public void removeOrgaRole(Membership member) {
    removeOrga(member.getPosition());
    target.getApplications().demote(member.getRole(), member.getPosition());
    updateBasedOnGivenRolesAndMembers();
  }


  private void updateBasedOnGivenRolesAndMembers() {
    target.getApplications().updateTeamRoleRole();
    target.getApplications().updateOrgaTeamRole();
  }

  public void handleCaptain(boolean b) {
    if (b) add(DiscordGroup.TEAM_CAPTAIN);
    else target.getApplications().updateTeamRoleRole();
  }
}
