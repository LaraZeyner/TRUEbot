package de.zahrie.trues.api.community.orgateam;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "orga_team")
@NamedQuery(name = "OrgaTeam.OrgaTeams.str", query = "SELECT nameCreation FROM OrgaTeam")
public class OrgaTeam implements Serializable {
  @Serial
  private static final long serialVersionUID = 4608926794336892138L;


  @Id
  @Column(name = "orga_team_id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "team", unique = true)
  @ToString.Exclude
  private Team team;

  public void setTeam(Team team) {
    team.setOrgaTeam(this);
    this.team = team;
    Database.update(team);
  }

  @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "team_role", nullable = false)
  @ToString.Exclude
  @Getter(AccessLevel.PACKAGE)
  private CustomDiscordGroup group;

  @Column(name = "team_name_created", nullable = false, length = 100)
  private String nameCreation;

  public void setNameCreation(String nameCreation) {
    getRoleManager().updateRoleName(nameCreation);
    this.nameCreation = nameCreation;
  }

  @Column(name = "team_abbr_created", nullable = false, length = 25)
  private String abbreviationCreation;

  @Column(name = "orga_place", columnDefinition = "TINYINT UNSIGNED null")
  private Short place = 0;

  @Column(name = "stand_ins", columnDefinition = "TINYINT UNSIGNED null")
  private Short standins = 4;

  public OrgaTeam(String nameCreation, String abbreviationCreation) {
    this.nameCreation = nameCreation;
    this.abbreviationCreation = abbreviationCreation;
  }

  public List<Membership> getMemberships() {
    return QueryBuilder.hql(Membership.class, "FROM Membership WHERE orgaTeam = :team").addParameter("team", this).list();
  }

  public Set<Membership> getActiveMemberships() {
    return getMemberships().stream().filter(Membership::isActive).collect(Collectors.toSet());
  }

  public Set<Membership> getMainMemberships() {
    return getMemberships().stream().filter(Membership::isActive).filter(membership -> membership.getRole().equals(TeamRole.MAIN)).collect(Collectors.toSet());
  }

  public String getName() {
    return team == null ? nameCreation : team.getName();
  }

  public String getAbbreviation() {
    return team == null ? abbreviationCreation : team.getAbbreviation();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final OrgaTeam orgaTeam)) return false;
    if (team != null) return getTeam().equals((orgaTeam.getTeam()));
    return getId() == orgaTeam.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  public OrgaTeamScheduler getScheduler() {
    return new OrgaTeamScheduler(this);
  }

  public OrgaTeamChannelHandler getChannels() {
    return new OrgaTeamChannelHandler(this);
  }

  public OrgaTeamRoleHandler getRoleManager() {
    return new OrgaTeamRoleHandler(this);
  }
}
