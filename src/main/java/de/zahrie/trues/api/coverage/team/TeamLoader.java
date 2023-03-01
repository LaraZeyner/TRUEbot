package de.zahrie.trues.api.coverage.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.player.PlayerHandler;
import de.zahrie.trues.api.coverage.player.PlayerLoader;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import de.zahrie.trues.util.util.Util;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 14.02.2023 for TRUEbot
 */
@Getter
public class TeamLoader extends GamesportsLoader {
  public static int idFromURL(String url) {
    return Integer.parseInt(Util.between(url, "/teams/", "-"));
  }

  private PrimeTeam team;

  public TeamLoader(@NotNull PrimeTeam team) {
    super(URLType.TEAM, team.getId());
    this.team = team;
  }

  TeamLoader(int teamId) {
    super(URLType.TEAM, teamId);
  }

  TeamLoader create() {
    final String teamTitle = html.find("h1").text();
    final String name = Util.between(teamTitle, null, " (", -1);
    final String abbreviation = Util.between(teamTitle, "(", ")", -1);
    this.team = TeamFactory.getTeam(id, name, abbreviation);
    return this;
  }

  public TeamHandler load() {
    final String teamTitle = html.find("h1").text();
    team.setName(Util.between(teamTitle, null, " (", -1));
    team.setAbbreviation(Util.between(teamTitle, "(", ")", -1));

    return TeamHandler.builder()
        .html(html)
        .url(url)
        .team(team)
        .players(getPlayers())
        .build();
  }

  private List<PrimePlayer> getPlayers() {
    final var players = new ArrayList<PrimePlayer>();
    for (HTML user : html.find("ul", "content-portrait-grid-l").findAll("li")) {
      final int primeId = Integer.parseInt(Util.between(user.find("a").getAttribute("href"), "/users/", "-"));
      final String summonerName = user.find("div", "txt-info").find("span").text();
      final var playerLoader = new PlayerLoader(primeId, summonerName);
      final PlayerHandler playerHandler = playerLoader.load();
      playerHandler.update();
      players.add(playerHandler.getPlayer());
    }

    team.getPlayers().stream().filter(player -> !players.contains((PrimePlayer) player)).filter(Objects::nonNull).forEach(player -> new PlayerLoader(((PrimePlayer) player).getPrmUserId(), player.getSummonerName()).handleLeftTeam());
    return players;
  }

}
