package de.zahrie.trues.api.coverage.team;

import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.database.Database;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class TeamFactory {
  public static PrimeTeam getTeam(int teamId) {
    PrimeTeam team = Database.Find.find(PrimeTeam.class, teamId);
    if (team != null) {
      return team;
    }

    team = new TeamLoader(teamId).create().getTeam();
    Database.save(team);
    return team;
  }


  public static PrimeTeam getTeam(int prmId, String name, String abbreviation) {
    PrimeTeam team = Database.Find.find(PrimeTeam.class, prmId);
    if (team != null) {
      return team;
    }

    team = fromName(name, abbreviation);
    if (team != null) {
      team.setPrmId(prmId);
      return team;
    }

    return new PrimeTeam(prmId, name, abbreviation);
  }

  @Nullable
  public static PrimeTeam fromName(String name, String abbreviation) {
    return Database.Find.find(PrimeTeam.class, new String[]{"name", "abbr"}, new Object[]{name, abbreviation}, "fromNameAbbr");
  }
}
