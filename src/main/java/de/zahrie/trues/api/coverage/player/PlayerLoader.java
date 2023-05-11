package de.zahrie.trues.api.coverage.player;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.Loader;
import de.zahrie.trues.api.coverage.player.model.PRMPlayer;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.io.request.URLType;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(StringUtils.class)
public class PlayerLoader extends GamesportsLoader implements Loader {
  public static int idFromURL(String url) {
    return url.between("/users/", "-").intValue();
  }

  private final PRMPlayer player;

  public PlayerLoader(int primeId, String summonerName) {
    super(URLType.PLAYER, primeId);
    this.player = PrimePlayerFactory.getPrimePlayer(primeId, summonerName);
  }

  public PlayerLoader(int primeId) {
    super(URLType.PLAYER, primeId);
    this.player = null;
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
    final PRMTeam team = TeamFactory.getTeam(teamId);
    player.setTeam(team);
  }

  public PRMPlayer handleMissingPlayer() {
    final int teamId = TeamLoader.idFromURL(html.find("ul", "content-icon-info-l")
        .find("li")
        .find("a")
        .getAttribute("href"));
    final TeamLoader teamLoader = new TeamLoader(teamId);
    return teamLoader.getPlayer(id);
  }

}
