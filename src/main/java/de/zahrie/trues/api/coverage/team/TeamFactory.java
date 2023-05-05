package de.zahrie.trues.api.coverage.team;

import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.query.Query;
import org.jetbrains.annotations.Nullable;

public class TeamFactory {

  @Nullable
  public static PRMTeam getTeam(int teamId) {
    final PRMTeam team = new Query<PRMTeam>().where("prm_id", teamId).entity();
    if (team != null) return team;

    final TeamLoader teamLoader = new TeamLoader(teamId).create();
    return teamLoader == null ? null : teamLoader.getTeam();
  }


  public static PRMTeam getTeam(int prmId, String name, String abbreviation) {
    PRMTeam team = new Query<PRMTeam>().where("prm_id", prmId).entity();
    if (team != null) return team;

    team = fromName(name, abbreviation);
    if (team != null) {
      team.setPrmId(prmId);
      return team;
    }

    return new PRMTeam(prmId, name, abbreviation);
  }

  @Nullable
  public static PRMTeam fromName(String name, String abbreviation) {
    return new Query<PRMTeam>().where("team_name", name).and("team_abbr", abbreviation).entity();
  }

  @Nullable
  public static PRMTeam fromAbbreviation(String abbreviation) {
    return new Query<PRMTeam>().where("team_abbr", abbreviation).entity();
  }

  @Nullable
  public static PRMTeam fromName(String name) {
    return new Query<PRMTeam>().where("team_name", name).entity();
  }
}
