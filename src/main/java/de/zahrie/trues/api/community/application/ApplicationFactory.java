package de.zahrie.trues.api.community.application;

import java.util.List;
import java.util.function.Predicate;

import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(DiscordUserFactory.class)
public class ApplicationFactory {
  /**
   * Erstelle eine neue Bewerbung für eine Position oder update bestehende Bewerbung
   *
   * @return neue Bewerbung
   */
  public static Application create(DiscordUser user, TeamRole role, TeamPosition position, @Nullable String appNotes, Boolean isWaiting) {
    Application application = QueryBuilder.hql(Application.class, "FROM Application WHERE user = :user AND position = :position").single();
    if (application == null) application = new Application(user, role, position);
    if (appNotes != null) application.setAppNotes(appNotes);
    application.setIsWaiting(isWaiting);
    Database.save(application);
    updateApplicationStatus(user);
    return application;
  }

  /**
   * Team tryoutet einen Bewerber für ihr Team
   *
   * @return neue Bewerbung
   */
  public static Application take(DiscordUser user, TeamRole role, TeamPosition position) {
    final Application application = ApplicationFactory.create(user, role, position, null, null);
    new RoleGranter(user).add(DiscordGroup.SUBSTITUDE, 14);
    return application;
  }

  /**
   * Nutzer wird aus dem Team entfernt und immer wieder zum Tryout
   *
   * @return neue Bewerbung
   */
  public static Application demote(DiscordUser user, TeamRole role, TeamPosition position) {
    return ApplicationFactory.create(user, role, position, null, true);
  }

  /**
   * Zustaendig für Tryout und Applicant
   * <pre>
   * ┌───────────────────┬──────────────────┬──────────────────┐
   * │                   │      Tryout      │     Bewerber     │
   * ├───────────────────┼──────────────────┼──────────────────┤
   * │ User accepted     │ aktive Bewerbung │ nie              │
   * │ User not accepted │ nie              │ aktive Bewerbung │
   * └───────────────────┴──────────────────┴──────────────────┘
   *  </pre>
   */
  public static void updateApplicationStatus(DiscordUser user) {
    final RoleGranter roleGranter = new RoleGranter(user);
    final boolean isWaiting = user.getApplications().stream().anyMatch(Application::getIsWaiting);
    if (user.getAcceptedBy() == null) {
      roleGranter.remove(DiscordGroup.TRYOUT);
      if (isWaiting) roleGranter.add(DiscordGroup.APPLICANT);
    } else {
      roleGranter.remove(DiscordGroup.APPLICANT);
      if (isWaiting) roleGranter.add(DiscordGroup.TRYOUT);
    }
  }

  /**
   * Zuständig für Teamrollen
   */
  public static void updateOrgaTeamRole(DiscordUser user) {
    final List<Membership> currentTeams = MembershipFactory.getCurrentTeams(user);
    final RoleGranter granter = new RoleGranter(user);
    final List<OrgaTeam> orgaTeams = QueryBuilder.hql(OrgaTeam.class, "FROM OrgaTeam").list();
    final List<OrgaTeam> currentOrgaTeams = currentTeams.stream().map(Membership::getOrgaTeam).toList();
    for (OrgaTeam orgaTeam : orgaTeams) {
      if (currentOrgaTeams.contains(orgaTeam) && !user.getMember().getRoles().contains(orgaTeam.getGroup().getRole())) {
        granter.addTeam(orgaTeam);
        continue;
      }
      if (!currentOrgaTeams.contains(orgaTeam) && user.getMember().getRoles().contains(orgaTeam.getGroup().getRole())) {
        granter.removeTeam(orgaTeam);
      }
    }
  }

  /**
   * Zuständig für TeamCaptain, Spieler, Mentor, Substitude,
   */
  public static void updateTeamRoleRole(DiscordUser user) {
    final List<Membership> currentTeams = MembershipFactory.getCurrentTeams(user);
    final RoleGranter granter = new RoleGranter(user);
    handleTeamRole(granter, currentTeams, DiscordGroup.TEAM_CAPTAIN, Membership::isCaptain);
    handleTeamRole(granter, currentTeams, DiscordGroup.SUBSTITUDE, member -> member.getRole().equals(TeamRole.SUBSTITUDE) || member.getRole().equals(TeamRole.TRYOUT));
    handleTeamRole(granter, currentTeams, DiscordGroup.MENTOR, member -> member.getRole().equals(TeamRole.MAIN) && member.getPosition().equals(TeamPosition.MENTOR));
    handleTeamRole(granter, currentTeams, DiscordGroup.PLAYER, member -> member.getRole().equals(TeamRole.MAIN) && member.getPosition().ordinal() < 5);
  }

  private static void handleTeamRole(RoleGranter granter, List<Membership> teams, DiscordGroup group, Predicate<Membership> predicate) {
    if (teams.stream().anyMatch(predicate)) granter.add(group);
    else granter.remove(group);
  }
}
