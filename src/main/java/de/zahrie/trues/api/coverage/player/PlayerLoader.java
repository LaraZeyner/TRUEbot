package de.zahrie.trues.api.coverage.player;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.Loader;
import de.zahrie.trues.util.io.request.URLType;

public class PlayerLoader extends GamesportsLoader implements Loader {
  public static int idFromURL(String url) {
    // TODO (Abgie) 15.03.2023: never used
    return Chain.of(url).between("/users/", "-").intValue();
  }

  private final PrimePlayer player;

  public PlayerLoader(int primeId, String summonerName) {
    super(URLType.PLAYER, primeId);
    this.player = PrimePlayerFactory.getPrimePlayer(primeId, summonerName);
  }

  @Override
  public PlayerHandler load() {
    return new PlayerHandler(url, player);
  }

  public void handleLeftTeam() {
    final int teamId = TeamLoader.idFromURL(html.find("ul", "content-icon-info-l")
        .find("li")
        .find("a")
        .getAttribute("href"));
    final PrimeTeam team = TeamFactory.getTeam(teamId);
    player.setTeam(team);
    Database.save(player);
    Database.save(team);
  }

}
