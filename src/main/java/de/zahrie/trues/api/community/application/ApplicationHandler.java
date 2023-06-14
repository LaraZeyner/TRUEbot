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
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public record ApplicationHandler(DiscordUser user) {
  public List<Application> list() {
    return new Query<>(Application.class).where("discord_user", user).entityList();
  }

  public List<Application> active() {
    return new Query<>(Application.class).where("discord_user", user).and("waiting", 1).entityList();
  }

  /**
   * Team tryoutet einen Bewerber für ihr Team
   *
   * @return neue Bewerbung
   */
  public Application take(@NonNull TeamRole role, @NonNull TeamPosition position) {
    final Application application = create(role, position, false);
    new RoleGranter(user).add(DiscordGroup.SUBSTITUTE, 14);
    return application;
  }

  /**
   * Nutzer wird aus dem Team entfernt und immer wieder zum Tryout
   *
   * @return neue Bewerbung
   */
  public Application demote(@NonNull TeamRole role, @NonNull TeamPosition position) {
    return create(role, position, true);
  }

  public Application create(@NonNull TeamRole role, @NonNull TeamPosition position, boolean active) {
    return create(role, position, null, active);
  }

  /**
   * Erstelle eine neue Bewerbung für eine Position oder update bestehende Bewerbung
   *
   * @return neue Bewerbung
   */
  public Application create(@NonNull TeamRole role, @NonNull TeamPosition position, @Nullable String appNotes, boolean active) {
    final Application existing = new Query<>(Application.class).where("discord_user").and("position", position).entity();
    if (existing != null) {
      if (List.of(TeamRole.ORGA_TRYOUT, TeamRole.TRYOUT).contains(role)) role = existing.getRole();
      if (appNotes == null) appNotes = existing.getAppNotes();
    } else if (List.of(TeamRole.ORGA_TRYOUT, TeamRole.TRYOUT).contains(role)) role = position.isOrga() ? TeamRole.ORGA : TeamRole.MAIN;

    final Application application = new Application(user, role, position, active, appNotes).create();
    updateApplicationStatus();
    return application;
  }

  /**
   * Zustaendig für Tryout und Applicant
   * <pre>
   *   Bewerber: User not accepted
   *   Tryout: User accepted
   *   </pre>
   */
  public void updateApplicationStatus() {
    final RoleGranter roleGranter = new RoleGranter(user);
    final boolean isActive = !user.getApplications().active().isEmpty();
    if (user.getAcceptedBy() == null) roleGranter.remove(DiscordGroup.TRYOUT);
    else if (isActive) roleGranter.add(DiscordGroup.TRYOUT);
  }

  /**
   * Zuständig für Teamrollen
   */
  public void updateOrgaTeamRole() {
    final List<Membership> currentTeams = MembershipFactory.getCurrentTeams(user);
    final List<OrgaTeam> currentOrgaTeams = currentTeams.stream().map(Membership::getOrgaTeam).toList();
    final RoleGranter granter = new RoleGranter(user);
    for (OrgaTeam orgaTeam : new Query<>(OrgaTeam.class).entityList()) {
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
   * Zuständig für TeamCaptain, Spieler, Substitute,
   */
  public void updateTeamRoleRole() {
    final List<Membership> currentTeams = MembershipFactory.getCurrentTeams(user);
    final RoleGranter granter = new RoleGranter(user);
    handleTeamRole(granter, currentTeams, DiscordGroup.TEAM_CAPTAIN, Membership::isCaptain);
    handleTeamRole(granter, currentTeams, DiscordGroup.SUBSTITUTE, member -> member.getRole().equals(TeamRole.SUBSTITUTE) || member.getRole().equals(TeamRole.TRYOUT));
    handleTeamRole(granter, currentTeams, DiscordGroup.PLAYER, member -> member.getRole().equals(TeamRole.MAIN) && member.getPosition().ordinal() < 5);
  }

  private static void handleTeamRole(RoleGranter granter, List<Membership> teams, DiscordGroup group, Predicate<Membership> predicate) {
    if (teams.stream().anyMatch(predicate)) granter.add(group);
    else granter.remove(group);
  }
}
