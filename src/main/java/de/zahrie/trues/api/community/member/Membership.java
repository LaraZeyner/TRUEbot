package de.zahrie.trues.api.community.member;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.database.types.TimeCoverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Managed die Aufgabe und Rolle in einem Team <br>
 * Man kann nur einmal pro Team auftreten.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "Membership")
@Table(name = "orga_member", indexes = {@Index(name = "idx_app", columnList = "discord_user, orga_team", unique = true)})
@NamedQuery(name = "Membership.ofUser", query = "FROM Membership WHERE user = :user AND active = true")
@NamedQuery(name = "Membership.ofUserCaptain", query = "FROM Membership WHERE user = :user AND active = true AND captain = true")
public class Membership implements Serializable, Comparable<Membership> {
  @Serial
  private static final long serialVersionUID = -6006729315935528279L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "lineup_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
  @JoinColumn(name = "discord_user", nullable = false)
  @ToString.Exclude
  private DiscordUser user;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "orga_team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", length = 15)
  private TeamRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "position", length = 15)
  private TeamPosition position;

  @Type(TimeCoverter.class)
  @Column(name = "timestamp")
  private Time timestamp = new Time();

  @Column(name = "captain")
  private boolean captain = false;

  @Column(name = "active")
  private boolean active = true;

  public Membership(DiscordUser user, TeamPosition position) {
    this(user, TeamRole.ORGA, position, null);
  }

  public Membership(DiscordUser user, TeamRole role, TeamPosition position, OrgaTeam orgaTeam) {
    this.user = user;
    this.orgaTeam = orgaTeam;
    this.role = role;
    this.position = position;
    this.timestamp = new Time();
    this.active = true;
    this.captain = false;
  }

  @Override
  public int compareTo(@NotNull Membership o) {
    return o.getRole().ordinal() - role.ordinal();
  }
}
