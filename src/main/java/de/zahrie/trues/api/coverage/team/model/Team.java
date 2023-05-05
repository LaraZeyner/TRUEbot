package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;

@Table(value = "team", department = "other")
//TODO (Abgie) 02.05.2023: Rename to TeamImpl
public class Team extends TeamBase implements Entity<Team> {
  @Serial
  private static final long serialVersionUID = 2619046560165024773L;


  public Team(String name, String abbreviation) {
    super(name, abbreviation);
  }

  public Team(int id, String name, String abbreviation, LocalDateTime refresh, boolean highlight, Integer lastMMR, Integer orgaTeamId) {
    super(id, name, abbreviation, refresh, highlight, lastMMR, orgaTeamId);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Team)) return false;
    if (this.name != null) return this.name.equals(((Team) obj).getName());
    return this.id == ((Team) obj).getId();
  }

  public static Team get(Object[] objects) {
    return new Team(
        (int) objects[0],
        (String) objects[2],
        (String) objects[3],
        (LocalDateTime) objects[4],
        (boolean) objects[5],
        (Integer) objects[6],
        new Query<OrgaTeam>().where("team", objects[0]).id()
    );
  }

  @Override
  public Team create() {
    final Team team = new Query<Team>().key("department", "other")
        .col("team_name", name).col("team_abbr", abbreviation).col("refresh", refresh).col("highlight", highlight)
        .col("last_team_mmr", lastMMR)
        .insert(this);
    if (orgaTeam != null) orgaTeam.setTeam(team);
    return team;
  }
}
