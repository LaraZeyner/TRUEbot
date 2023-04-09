package de.zahrie.trues.api.coverage.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.player.PlayerHandler;
import de.zahrie.trues.api.coverage.player.PlayerLoader;
import de.zahrie.trues.api.coverage.player.model.PRMPlayer;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@Getter
@ExtensionMethod(StringUtils.class)
public class TeamLoader extends GamesportsLoader {
  public static int idFromURL(String url) {
    return Integer.parseInt(url.between("/teams/", "-"));
  }

  private PRMTeam team;

  public TeamLoader(@NotNull PRMTeam team) {
    super(URLType.TEAM, team.getId());
    this.team = team;
  }

  TeamLoader(int teamId) {
    super(URLType.TEAM, teamId);
  }

  TeamLoader create() {
    if (html == null) {
      return null;
    }
    final String teamTitle = html.find("h1").text();
    final String name = teamTitle.between(null, " (", -1);
    final String abbreviation = teamTitle.between("(", ")", -1);
    this.team = TeamFactory.getTeam(id, name, abbreviation);
    return this;
  }

  public TeamHandler load() {
    final String teamTitle = html.find("h1").text();
    team.setName(teamTitle.between(null, " (", -1));
    team.setAbbreviation(teamTitle.between("(", ")", -1));

    return TeamHandler.builder()
        .html(html)
        .url(url)
        .team(team)
        .players(getPlayers())
        .build();
  }

  private List<PRMPlayer> getPlayers() {
    final var players = new ArrayList<PRMPlayer>();
    for (HTML user : html.find("ul", "content-portrait-grid-l").findAll("li")) {
      final int primeId = user.find("a").getAttribute("href").between("/users/", "-").intValue();
      final String summonerName = user.find("div", "txt-info").find("span").text();
      final var playerLoader = new PlayerLoader(primeId, summonerName);
      final PlayerHandler playerHandler = playerLoader.load();
      playerHandler.update();
      players.add((PRMPlayer) playerHandler.getPlayer());
    }

    team.getPlayers().stream().filter(player -> !players.contains((PRMPlayer) player)).filter(Objects::nonNull).forEach(player -> new PlayerLoader(((PRMPlayer) player).getPrmUserId(), player.getSummonerName()).handleLeftTeam());
    return players;
  }

}
