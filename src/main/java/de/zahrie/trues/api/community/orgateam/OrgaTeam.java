package de.zahrie.trues.api.community.orgateam;

import java.io.Serial;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@Setter
@Table("orga_team")
public class OrgaTeam implements Entity<OrgaTeam>, Comparable<OrgaTeam> {
  @Serial
  private static final long serialVersionUID = 5847570695211918386L;
  private int id; // orga_team_id
  private String nameCreation; // team_name_created
  private String abbreviationCreation; // team_abbr_created
  @Getter(AccessLevel.PACKAGE)
  private CustomDiscordGroup group; // team_role
  private TeamBase team; // team
  private Byte place = 0; // orga_place
  private Byte standins = 4; // stand_ins

  public OrgaTeam(String nameCreation, String abbreviationCreation) {
    this.nameCreation = nameCreation;
    this.abbreviationCreation = abbreviationCreation;
  }

  public static OrgaTeam get(Object[] objects) {
    return new OrgaTeam(
        (int) objects[0],
        (String) objects[1],
        (String) objects[2],
        new Query<CustomDiscordGroup>().entity( objects[3]),
        new Query<TeamBase>().entity(objects[4]),
        (Byte) objects[5],
        (Byte) objects[6]
    );
  }

  @Nullable
  public static OrgaTeam fromName(@NonNull String name) {
    return new Query<OrgaTeam>().where("team_name_created", name).entity();
  }

  @Override
  public OrgaTeam create() {
    final OrgaTeam orgaTeam = new Query<OrgaTeam>().key("orga_team_id", id)
        .col("team_name_created", nameCreation).col("team_abbr_created", abbreviationCreation).col("team_role", group)
        .col("team", team).col("orga_place", place).col("stand_ins", standins).insert(this);
    team.setOrgaTeam(this);
    return orgaTeam;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public void setNameCreation(String nameCreation) {
    final boolean updated = !this.nameCreation.equals(nameCreation);
    this.nameCreation = nameCreation;
    if (updated) {
      getRoleManager().updateRoleName(nameCreation);
      getChannels().updateChannels();
      new Query<OrgaTeam>().col("team_name_created", nameCreation).update(id);
    }
  }

  public void setAbbreviationCreation(String abbreviationCreation) {
    final boolean updated = !this.abbreviationCreation.equals(abbreviationCreation);
    this.abbreviationCreation = abbreviationCreation;
    if (updated) {
      getChannels().updateChannels();
      new Query<OrgaTeam>().col("team_abbr_created", nameCreation).update(id);
    }
  }

  public void setGroup(CustomDiscordGroup group) {
    this.group = group;
    new Query<OrgaTeam>().col("team_role", group).update(id);
  }

  public void setTeam(TeamBase team) {
    team.setOrgaTeam(this);
    this.team = team;
    new Query<OrgaTeam>().col("team", team).update(id);
  }

  public List<Membership> getActiveMemberships() {
    return new Query<Membership>().where("orga_team", this).and("active", true).entityList();
  }

  public List<Membership> getMainMemberships() {
    return new Query<Membership>().where("orga_team", this).and("active", true).and("role", TeamRole.MAIN)
        .entityList();
  }

  public Membership getMembership(TeamRole role, TeamPosition position) {
    return new Query<Membership>().where("orga_team", team).and("active", true).and("role", role).and("position", position).entity();
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
    if (!(o instanceof OrgaTeam orgaTeam)) return false;
    if (team != null) return getTeam().equals((orgaTeam.getTeam()));
    return getId() == orgaTeam.getId();
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

  @Override
  public int compareTo(@NotNull OrgaTeam o) {
    return Comparator.comparing(OrgaTeam::getPlace)
        .thenComparing(OrgaTeam::getId).compare(this, o);
  }
}
