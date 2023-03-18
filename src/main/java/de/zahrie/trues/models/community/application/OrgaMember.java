package de.zahrie.trues.models.community.application;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.models.community.OrgaTeam;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "OrgaMember")
@Table(name = "orga_member", indexes = {@Index(name = "idx_app", columnList = "discord_user, lineup_role, lane, orga_team", unique = true)})
@NamedQuery(name = "OrgaMember.ofMember", query = "FROM OrgaMember WHERE member = :member AND active = true")
@NamedQuery(name = "OrgaMember.ofMemberCaptain", query = "FROM OrgaMember WHERE member = :member AND active = true AND captain = true")
public class OrgaMember implements Serializable, Comparable<OrgaMember> {
  @Serial
  private static final long serialVersionUID = -6006729315935528279L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "lineup_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "discord_user", nullable = false)
  @ToString.Exclude
  private DiscordMember member;

  @ManyToOne(fetch = FetchType.LAZY)
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

  public OrgaMember(DiscordMember member, OrgaTeam orgaTeam, TeamRole role, TeamPosition position) {
    this.member = member;
    this.orgaTeam = orgaTeam;
    this.role = role;
    this.position = position;
    this.timestamp = new Time();
    this.active = true;
    this.captain = false;
  }

  @Override
  public int compareTo(@NotNull OrgaMember o) {
    return o.getRole().ordinal() - role.ordinal();
  }
}
