package de.zahrie.trues.api.discord.group;

import java.util.List;

import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.community.application.OrgaMemberFactory;
import de.zahrie.trues.models.community.application.TeamRole;

public class RoleGranter extends RoleGranterBase {
  public RoleGranter(DiscordMember target) {
    super(target);
  }

  public RoleGranter(DiscordMember target, DiscordMember invoker) {
    super(target, invoker);
  }

  public void addTeamRole(TeamRole role, OrgaTeam team) {
    if (team != null) {
      addTeam(team);
      //TODO (Abgie) 14.03.2023: Maybe Remove after 14 Days
    }

    switch (role) {
      case TRYOUT -> add(DiscordGroup.SUBSTITUDE, 14);
      case SUBSTITUDE -> {
        add(DiscordGroup.SUBSTITUDE);
        remove(DiscordGroup.TRYOUT);
      }
      case MAIN -> {
        add(DiscordGroup.PLAYER);
        remove(DiscordGroup.TRYOUT);
        remove(DiscordGroup.SUBSTITUDE);
      }
    }
  }

  public void removeTeamRole(TeamRole role, OrgaTeam team) {
    if (team != null) {
      removeTeam(team);
    }
    add(DiscordGroup.TRYOUT);
    updateBasedOnGivenRoles();
  }

  public void updateBasedOnGivenRolesAndMembers(OrgaTeam team) {
    updateBasedOnGivenRoles();
    updateBasedOnMembers();
  }

  private void updateBasedOnGivenRoles() {
    final List<OrgaMember> currentTeams = OrgaMemberFactory.getCurrentTeams(target);
    if (currentTeams.stream().anyMatch(OrgaMember::isCaptain)) {
      add(DiscordGroup.TEAM_CAPTAIN);
    } else {
      remove(DiscordGroup.TEAM_CAPTAIN);
    }

    if (currentTeams.stream().anyMatch(member -> member.getRole().equals(TeamRole.MAIN))) {
      add(DiscordGroup.PLAYER);
      remove(DiscordGroup.SUBSTITUDE);
      remove(DiscordGroup.TRYOUT);
      return;
    } else {
      remove(DiscordGroup.PLAYER);
    }

    if (currentTeams.stream().anyMatch(member -> member.getRole().equals(TeamRole.SUBSTITUDE))) {
      add(DiscordGroup.SUBSTITUDE);
      remove(DiscordGroup.TRYOUT);
    } else {
      remove(DiscordGroup.SUBSTITUDE);
    }
  }

  private void updateBasedOnMembers() {
    final List<OrgaMember> currentTeams = OrgaMemberFactory.getCurrentTeams(target);
    final List<OrgaTeam> orgaTeams = Database.Find.findList(OrgaTeam.class);
    final List<OrgaTeam> currentOrgaTeams = currentTeams.stream().map(OrgaMember::getOrgaTeam).toList();
    for (final OrgaTeam orgaTeam : orgaTeams) {
      if (currentOrgaTeams.contains(orgaTeam) && !target.getMember().getRoles().contains(orgaTeam.getRole())) {
        addTeam(orgaTeam);
        continue;
      }
      if (!currentOrgaTeams.contains(orgaTeam) && target.getMember().getRoles().contains(orgaTeam.getRole())) {
        removeTeam(orgaTeam);
      }
    }
  }

  public void handleCaptain(boolean b) {
    if (b) {
      add(DiscordGroup.TEAM_CAPTAIN);
    } else {
      updateBasedOnGivenRoles();
    }
  }
}
