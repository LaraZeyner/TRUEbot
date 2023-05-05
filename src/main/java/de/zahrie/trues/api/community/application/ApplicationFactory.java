package de.zahrie.trues.api.community.application;

import java.util.List;
import java.util.function.Predicate;

import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(DiscordUserFactory.class)
public class ApplicationFactory {
  public static Application create(@NonNull DiscordUser user, @NonNull TeamRole role, @NonNull TeamPosition position) {
    return create(user, role, position, null, null);
  }

  /**
   * Erstelle eine neue Bewerbung für eine Position oder update bestehende Bewerbung
   *
   * @return neue Bewerbung
   */
  public static Application create(@NonNull DiscordUser user, @NonNull TeamRole role, @NonNull TeamPosition position, @Nullable String appNotes, @Nullable Boolean isWaiting) {
    final Application application = new Application(user, role, position, isWaiting, appNotes).create();
    updateApplicationStatus(user);
    return application;
  }

  /**
   * Team tryoutet einen Bewerber für ihr Team
   *
   * @return neue Bewerbung
   */
  public static Application take(DiscordUser user, TeamRole role, TeamPosition position) {
    final Application application = create(user, role, position);
    new RoleGranter(user).add(DiscordGroup.SUBSTITUDE, 14);
    return application;
  }

  /**
   * Nutzer wird aus dem Team entfernt und immer wieder zum Tryout
   *
   * @return neue Bewerbung
   */
  public static Application demote(DiscordUser user, TeamRole role, TeamPosition position) {
    return create(user, role, position);
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
    final boolean isWaiting = user.getApplications().stream().anyMatch(Application::getWaiting);
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
    final List<OrgaTeam> currentOrgaTeams = currentTeams.stream().map(Membership::getOrgaTeam).toList();
    final RoleGranter granter = new RoleGranter(user);
    for (OrgaTeam orgaTeam : new Query<OrgaTeam>().entityList()) {
      if (currentOrgaTeams.contains(orgaTeam) && !user.getMember().getRoles().contains(orgaTeam.getRoleManager().getRole())) {
        granter.addTeam(orgaTeam);
        continue;
      }
      if (!currentOrgaTeams.contains(orgaTeam) && user.getMember().getRoles().contains(orgaTeam.getRoleManager().getRole())) {
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
    handleTeamRole(granter, currentTeams, DiscordGroup.SUBSTITUDE, member -> member.getRole().equals(TeamRole.SUBSTITUTE) || member.getRole().equals(TeamRole.TRYOUT));
    handleTeamRole(granter, currentTeams, DiscordGroup.MENTOR, member -> member.getRole().equals(TeamRole.MAIN) && member.getPosition().equals(TeamPosition.MENTOR));
    handleTeamRole(granter, currentTeams, DiscordGroup.PLAYER, member -> member.getRole().equals(TeamRole.MAIN) && member.getPosition().ordinal() < 5);
  }

  private static void handleTeamRole(RoleGranter granter, List<Membership> teams, DiscordGroup group, Predicate<Membership> predicate) {
    if (teams.stream().anyMatch(predicate)) granter.add(group);
    else granter.remove(group);
  }
}
