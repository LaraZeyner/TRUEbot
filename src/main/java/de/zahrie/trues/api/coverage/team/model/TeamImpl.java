package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Table(value = "team", department = "other")
public class TeamImpl extends Team implements Entity<TeamImpl> {
  @Serial
  private static final long serialVersionUID = 2619046560165024773L;

  public TeamImpl(String name, String abbreviation) {
    super(name, abbreviation);
  }

  public TeamImpl(int id, String name, String abbreviation, LocalDateTime refresh, boolean highlight, Integer lastMMR, Integer orgaTeamId) {
    super(id, name, abbreviation, refresh, highlight, lastMMR, orgaTeamId);
  }

  public static TeamImpl get(List<Object> objects) {
    return new TeamImpl(
        (int) objects.get(0),
        (String) objects.get(2),
        (String) objects.get(3),
        (LocalDateTime) objects.get(4),
        (boolean) objects.get(5),
        (Integer) objects.get(6),
        new Query<>(OrgaTeam.class).where("team", objects.get(0)).id()
    );
  }

  @Override
  public TeamImpl create() {
    final TeamImpl team = new Query<>(TeamImpl.class)
        .col("team_name", name).col("team_abbr", abbreviation).col("refresh", refresh).col("highlight", highlight)
        .col("last_team_mmr", lastMMR)
        .insert(this);
    if (orgaTeam != null) orgaTeam.setTeam(team);
    return team;
  }
}
