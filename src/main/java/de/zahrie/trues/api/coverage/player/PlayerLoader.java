package de.zahrie.trues.api.coverage.player;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.Loader;
import de.zahrie.trues.util.util.Util;
import de.zahrie.trues.util.database.Database;
import de.zahrie.trues.util.io.request.URLType;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class PlayerLoader extends GamesportsLoader implements Loader {
  //TODO (Abgie) 27.02.2023:
  public static int idFromURL(String url) {
    return Integer.parseInt(Util.between(url, "/users/", "-"));
  }

  private final PrimePlayer player;

  public PlayerLoader(int primeId, String summonerName) {
    super(URLType.PLAYER, primeId);
    this.player = PlayerFactory.getPrimePlayer(primeId, summonerName);
  }

  @Override
  public PlayerHandler load() {
    return new PlayerHandler(url, player);
  }

  public void handleLeftTeam() {
    int teamId = TeamLoader.idFromURL(html.find("ul", "content-icon-info-l")
        .find("li")
        .find("a")
        .getAttribute("href"));
    final PrimeTeam team = TeamFactory.getTeam(teamId);
    player.setTeam(team);
    Database.save(player);
    Database.save(team);
  }

}
