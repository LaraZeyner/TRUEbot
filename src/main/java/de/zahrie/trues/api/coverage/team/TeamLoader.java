package de.zahrie.trues.api.coverage.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.player.PlayerHandler;
import de.zahrie.trues.api.coverage.player.PlayerLoader;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class TeamLoader extends GamesportsLoader {
  public static int idFromURL(Chain url) {
    return Integer.parseInt(url.between("/teams/", "-").toString());
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
    final Chain teamTitle = html.find("h1").text();
    final Chain name = teamTitle.between(null, " (", -1);
    final Chain abbreviation = teamTitle.between("(", ")", -1);
    this.team = TeamFactory.getTeam(id, name.toString(), abbreviation.toString());
    return this;
  }

  public TeamHandler load() {
    final Chain teamTitle = html.find("h1").text();
    team.setName(teamTitle.between(null, " (", -1).toString());
    team.setAbbreviation(teamTitle.between("(", ")", -1).toString());

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
      final int primeId = user.find("a").getAttribute("href").between("/users/", "-").intValue();
      final Chain summonerName = user.find("div", "txt-info").find("span").text();
      final var playerLoader = new PlayerLoader(primeId, summonerName.toString());
      final PlayerHandler playerHandler = playerLoader.load();
      playerHandler.update();
      players.add((PrimePlayer) playerHandler.getPlayer());
    }

    team.getPlayers().stream().filter(player -> !players.contains((PrimePlayer) player)).filter(Objects::nonNull).forEach(player -> new PlayerLoader(((PrimePlayer) player).getPrmUserId(), player.getSummonerName()).handleLeftTeam());
    return players;
  }

}
