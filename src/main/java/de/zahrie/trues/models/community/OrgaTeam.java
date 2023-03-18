package de.zahrie.trues.models.community;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.group.CustomDiscordRole;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.discord.Nunu;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.community.application.OrgaMemberFactory;
import de.zahrie.trues.models.community.application.TeamPosition;
import de.zahrie.trues.models.community.application.TeamRole;
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
import net.dv8tion.jda.api.entities.Role;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "orga_team")
@NamedQuery(name = "OrgaTeam.findCategory", query = "FROM OrgaTeam WHERE category.discordId = :categoryId")
public class OrgaTeam implements Serializable {
  @Serial
  private static final long serialVersionUID = 4608926794336892138L;


  @Id
  @Column(name = "orga_team_id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  @ToString.Exclude
  private Team team;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "team_role", nullable = false)
  @ToString.Exclude
  private CustomDiscordRole role;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_channel", nullable = false)
  @ToString.Exclude
  private DiscordChannel category;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "chat_channel", nullable = false)
  @ToString.Exclude
  private DiscordChannel chat;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "voice_channel", nullable = false)
  @ToString.Exclude
  private DiscordChannel voice;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "info_channel", nullable = false)
  @ToString.Exclude
  private DiscordChannel info;

  @Column(name = "team_name_created", nullable = false, length = 100)
  private String nameCreation;

  @Column(name = "team_abbr_created", nullable = false, length = 25)
  private String abbreviationCreation;

  @OneToMany(mappedBy = "orgaTeam")
  @ToString.Exclude
  private Set<OrgaMember> orgaMembers = new LinkedHashSet<>();

  public String getName() {
    return team == null ? nameCreation : team.getName();
  }

  public String getAbbreviation() {
    // TODO (Abgie) 15.03.2023: never used
    return team == null ? abbreviationCreation : team.getAbbreviation();
  }

  public void addRole(DiscordMember member, TeamRole role, TeamPosition position) {
    if (role.equals(TeamRole.MAIN)) {
      final OrgaMember mainOnPosition = OrgaMemberFactory.getOfTeam(this, role, position);
      if (mainOnPosition != null) {
        mainOnPosition.setRole(TeamRole.SUBSTITUDE);
        Database.save(mainOnPosition);
      }
    }

    OrgaMember orgaMember = OrgaMemberFactory.getOfTeam(member, this);
    if (orgaMember == null) {
      orgaMember = new OrgaMember(member, this, role, position);
    } else {
      orgaMember.setOrgaTeam(this);
      orgaMember.setRole(role);
      orgaMember.setPosition(position);
    }
    Database.save(orgaMember);
    Database.save(this);

    final var granter = new RoleGranter(member);
    granter.addTeamRole(role, this);
  }

  public void addCaptain(DiscordMember member) {
    final OrgaMember orgaMember = OrgaMemberFactory.getOfTeam(member, this);
    orgaMember.setCaptain(true);
    Database.save(orgaMember);
    final var granter = new RoleGranter(member);
    granter.handleCaptain(true);
  }

  public void removeCaptain(DiscordMember member) {
    final OrgaMember orgaMember = OrgaMemberFactory.getOfTeam(member, this);
    orgaMember.setCaptain(false);
    Database.save(orgaMember);
    final var granter = new RoleGranter(member);
    granter.handleCaptain(false);
  }

  public void removeRole(DiscordMember member) {
    final OrgaMember orgaMember = OrgaMemberFactory.getOfTeam(member, this);
    if (orgaMember == null) {
      return;
    }
    orgaMember.setActive(false);
    Database.save(orgaMember);

    final var granter = new RoleGranter(member);
    granter.removeTeamRole(orgaMember.getRole(), this);
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

  public Role getRole() {
    return Nunu.DiscordRole.getRole(this);
  }

  public CustomDiscordRole getCustomRole() {
    return role;
  }
}
