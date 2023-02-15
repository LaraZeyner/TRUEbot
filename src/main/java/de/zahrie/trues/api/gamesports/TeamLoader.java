package de.zahrie.trues.api.gamesports;

import de.zahrie.trues.models.team.PrimeTeam;
import de.zahrie.trues.util.Loader;
import de.zahrie.trues.util.Util;
import de.zahrie.trues.util.database.Database;
import de.zahrie.trues.util.io.request.URLType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 14.02.2023 for TRUEbot
 */
@Getter
public class TeamLoader extends GamesportsLoader implements Loader {
  public static int idFromURL(String url) {
    return Integer.parseInt(Util.between(url, "/teams/", "-"));
  }

  private final PrimeTeam team;

  public TeamLoader(@NotNull PrimeTeam team) {
    super(URLType.MATCH, team.getId());
    this.team = team;
  }

  public TeamLoader(int teamId) {
    super(URLType.TEAM, teamId);
    this.team = Database.find(PrimeTeam.class, teamId);
  }

  @Override
  public void load() {
    System.out.println("TESTING");
  }
}
