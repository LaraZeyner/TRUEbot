package de.zahrie.trues.api.community.member;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * Managed die Aufgabe und Rolle in einem Team <br>
 * Man kann nur einmal pro Team auftreten.
 */
@AllArgsConstructor
@Getter
@Table("orga_member")
@ExtensionMethod(StringUtils.class)
public class Membership implements Entity<Membership>, Comparable<Membership> {
  @Serial
  private static final long serialVersionUID = 3193091569421460896L;

  private int id; // orga_member_id
  private final DiscordUser user; // discord_user
  private OrgaTeam orgaTeam; // orga_team
  private TeamRole role; // role
  private TeamPosition position; // position
  private LocalDateTime timestamp = LocalDateTime.now(); // timestamp
  private boolean captain = false; // captain
  private boolean active = true; // active


  public Membership(DiscordUser user, TeamPosition position) {
    this(user, null, TeamRole.ORGA, position);
  }

  Membership(DiscordUser user, OrgaTeam team, TeamRole role, TeamPosition position) {
    this.user = user;
    this.orgaTeam = team;
    this.role = role;
    this.position = position;
  }

  public static Membership get(List<Object> objects) {
    return new Membership(
        (int) objects.get(0),
        new Query<>(DiscordUser.class).entity( objects.get(1)),
        new Query<>(OrgaTeam.class).forId((int) objects.get(2)).entity(),
        new SQLEnum<>(TeamRole.class).of(objects.get(3)),
        new SQLEnum<>(TeamPosition.class).of(objects.get(4)),
        (LocalDateTime) objects.get(5),
        (boolean) objects.get(6),
        (boolean) objects.get(7)
    );
  }

  @Override
  public Membership create() {
    return new Query<>(Membership.class).key("discord_user", user).key("orga_team", orgaTeam)
        .col("position", position).col("role", role).col("timestamp", timestamp).col("captain", captain).col("active", active).insert(this);
  }

  public void removeFromTeam(OrgaTeam team) {
    this.orgaTeam = null;
    this.active = false;
    new Query<>(Membership.class).col("orga_team", null).col("active", false).update(id);
    new RoleGranter(user).removeTeamRole(this, team);
    Nunu.DiscordMessager.dm(user, "Du wurdest aus dem Team **" + team.getName() + "** entfernt. Du kannst aber jederzeit gerne eine neue Bewerbung schreiben. Solltest du Probleme oder Fragen haben kannst du mir jederzeit schreiben.");
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setCaptain(boolean captain) {
    if (this.captain != captain) new Query<>(Membership.class).col("captain", captain).update(id);
    this.captain = captain;
    new RoleGranter(user).handleCaptain(captain);
  }

  public void updateRoleAndPosition(TeamRole role, TeamPosition position) {
    this.role = role;
    this.position = position;
    this.active = true;
    new Query<>(Membership.class).col("role", role).col("position", position).col("active", true).update(id);
  }

  public void setRole(TeamRole role) {
    this.role = role;
    new Query<>(Membership.class).col("role", role).update(id);
  }

  public String getPositionString() {
    return (role.equals(TeamRole.MAIN) ? "" : role.name().capitalizeFirst() + " ") + position.name().capitalizeFirst();
  }

  @Override
  public int compareTo(@NotNull Membership o) {
    return Comparator.comparing(Membership::isActive)
        .thenComparing(Membership::getRole, Comparator.reverseOrder())
        .thenComparing(Membership::getPosition).compare(this, o);
  }
}
