package de.zahrie.trues.api.community.orgateam;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "team", unique = true)
  @ToString.Exclude
  private Team team;

  @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "team_role", nullable = false)
  @ToString.Exclude
  private CustomDiscordGroup group;

  @Column(name = "team_name_created", nullable = false, length = 100)
  private String nameCreation;

  @Column(name = "team_abbr_created", nullable = false, length = 25)
  private String abbreviationCreation;

  @Column(name = "orga_place", columnDefinition = "TINYINT UNSIGNED null")
  private Short place = 0;

  @OneToMany(mappedBy = "orgaTeam")
  @ToString.Exclude
  private Set<TeamChannel> teamChannels;

  @OneToMany(mappedBy = "orgaTeam")
  @ToString.Exclude
  private Set<Membership> memberships = new LinkedHashSet<>();

  public OrgaTeam(String nameCreation, String abbreviationCreation) {
    this.nameCreation = nameCreation;
    this.abbreviationCreation = abbreviationCreation;
  }

  public String getName() {
    return team == null ? nameCreation : team.getName();
  }

  public String getAbbreviation() {
    return team == null ? abbreviationCreation : team.getAbbreviation();
  }

  public TeamChannel getChannelOf(TeamChannelType teamChannelType) {
    return teamChannels.stream().filter(teamChannel -> teamChannel.getTeamChannelType().equals(teamChannelType)).findFirst().orElse(null);
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
}
