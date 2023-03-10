package de.zahrie.trues.api.coverage.league;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.playday.PlaydayFactory;
import de.zahrie.trues.api.coverage.season.PrimeSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class LeagueLoader extends GamesportsLoader {
  public static League season(Chain url, Chain name) {
    final int seasonId = Integer.parseInt(url.between("/prm/", "-").toString());
    final int stageId = Integer.parseInt(url.between("/group/", "-").toString());
    final PrimeSeason season = SeasonFactory.getSeason(seasonId);
    return LeagueFactory.getGroup(season, name.toString(), stageId);
  }

  public static String divisionNameFromURL(String url) {
    Chain section = Chain.of(url).between("/", null, -1).between("-").replace("-", " ");
    if (section.startsWith("division ")) {
      section = section.replace(".", section.lastIndexOf(" "));
    }
    return section.capitalizeFirst().toString();
  }

  public static Integer stageIdFromUrl(String url) {
    return Chain.of(url).between("/group/", "-").intValue();
  }

  private final League league;
  private final String url;

  public LeagueLoader(@NotNull String url) {
    super(URLType.LEAGUE, Chain.of(url).between("/prm/", "-").intValue(), Chain.of(url).between("/group/", "-").intValue(),
        Chain.of(url).between("/", "-", -1).intValue());
    final PrimeSeason season = SeasonFactory.getSeason(Chain.of(url).between("/prm/", "-").intValue());
    this.league = LeagueFactory.getGroup(season, divisionNameFromURL(url), stageIdFromUrl(url));
    this.url = url;
  }

  public LeagueHandler load() {
    return LeagueHandler.builder()
        .url(url)
        .league(league)
        .teams(getTeams())
        .playdays(getPlaydays())
        .build();
  }

  @NotNull
  private List<PrimeTeam> getTeams() {
    return html.find("tbody")
        .findAll("tr").stream()
        .map(row -> row.findAll("td").get(1))
        .map(cell -> cell.find("a").getAttribute("href"))
        .map(TeamLoader::idFromURL)
        .map(TeamFactory::getTeam)
        .toList();
  }

  @NotNull
  private List<LeaguePlayday> getPlaydays() {
    final Chain leagueName = html.find("h1").text().between(":");

    if (leagueName.toString().equals(Const.Gamesports.STARTER_NAME)) {
      return List.of();
    }

    final List<LeaguePlayday> playdays = new ArrayList<>();
    final List<HTML> findAllByClass = html.findAll("div", "widget-ticker");
    for (int i = 0; i < findAllByClass.size(); i++) {
      final HTML playdayHTML = findAllByClass.get(i);
      final List<PrimeMatch> primeMatches = playdayHTML.findAll("tr").stream()
          .map(match -> match.find("a").getAttribute("href"))
          .map(MatchLoader::idFromURL)
          .filter(Objects::nonNull)
          .map(MatchFactory::getMatch)
          .toList();
      final var playday = new LeaguePlayday(PlaydayFactory.getPlayday(league.getStage(), i + 1), primeMatches);
      playdays.add(playday);
    }
    return playdays;
  }

}
