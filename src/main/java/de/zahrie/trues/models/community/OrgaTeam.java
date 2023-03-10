package de.zahrie.trues.models.community;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.models.community.application.ApplicationRole;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.community.application.OrgaMemberFactory;
import de.zahrie.trues.models.community.application.TeamPosition;
import de.zahrie.trues.models.community.application.TeamRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

  @Column(name = "discord_role", nullable = false)
  private long roleId;

  @Column(name = "discord_category", nullable = false)
  private long categoryId;

  @Column(name = "discord_chat", nullable = false)
  private long chatId;

  @Column(name = "discord_voice", nullable = false)
  private long voiceId;

  @Column(name = "discord_intern")
  private Long internId;

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
    return team == null ? abbreviationCreation : team.getAbbreviation();
  }

  public void addRole(DiscordMember member, ApplicationRole role, TeamPosition position) {
    if (role.equals(ApplicationRole.MAIN)) {
      final OrgaMember mainOnPosition = OrgaMemberFactory.getOfTeam(this, TeamRole.MAIN, position);
      if (mainOnPosition != null) {
        mainOnPosition.setRole(TeamRole.SUBSTITUDE);
        Database.save(mainOnPosition);
      }
    }

    OrgaMember orgaMember = OrgaMemberFactory.getOfTeam(member, this);
    if (orgaMember == null) {
      orgaMember = new OrgaMember(member, this, TeamRole.valueOf(role.name()), position);
    } else {
      orgaMember.setOrgaTeam(this);
      orgaMember.setRole(TeamRole.valueOf(role.name()));
      orgaMember.setPosition(position);
    }
    Database.save(orgaMember);
    Database.save(this);

    var granter = new RoleGranter(member);
    granter.remove(DiscordGroup.TRYOUT);
    switch (role) {
      case TRYOUT -> granter.add(DiscordGroup.ACCEPTED, 14);
      case SUBSTITUDE, MAIN -> {
        granter.add(DiscordGroup.ACCEPTED);
        granter.remove(DiscordGroup.TRYOUT);
      }
    }

    granter.addTeam(this);
  }

  public void removeRole(DiscordMember member) {
    final OrgaMember orgaMember = OrgaMemberFactory.getOfTeam(member, this);
    if (orgaMember == null) {
      return;
    }
    orgaMember.setActive(false);
    Database.save(orgaMember);

    var granter = new RoleGranter(member);
    granter.add(DiscordGroup.TRYOUT);
    granter.remove(DiscordGroup.ACCEPTED);

    granter.removeTeam(this);
  }
}
