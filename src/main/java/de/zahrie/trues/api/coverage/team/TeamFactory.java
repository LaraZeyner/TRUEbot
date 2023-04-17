package de.zahrie.trues.api.coverage.team;

import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import org.jetbrains.annotations.Nullable;

public class TeamFactory {

  @Nullable
  public static PRMTeam getTeam(int teamId) {
    PRMTeam team = QueryBuilder.hql(PRMTeam.class, "FROM PRMTeam WHERE prmId = " + teamId).single();
    if (team != null) return team;

    final TeamLoader teamLoader = new TeamLoader(teamId).create();
    if (teamLoader == null) return null;

    team = teamLoader.getTeam();
    Database.update(team);
    return team;
  }


  public static PRMTeam getTeam(int prmId, String name, String abbreviation) {
    PRMTeam team = Database.Find.find(PRMTeam.class, prmId);
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
    return QueryBuilder.hql(PRMTeam.class, "FROM PRMTeam WHERE name = " + name + " and abbreviation = " + abbreviation).single();
  }

  @Nullable
  public static PRMTeam fromAbbreviation(String abbreviation) {
    return QueryBuilder.hql(PRMTeam.class, "FROM PRMTeam WHERE abbreviation = " + abbreviation).single();
  }

  @Nullable
  public static PRMTeam fromName(String name) {
    return QueryBuilder.hql(PRMTeam.class, "FROM PRMTeam WHERE name = " + name).single();
  }
}
