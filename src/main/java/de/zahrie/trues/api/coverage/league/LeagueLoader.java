package de.zahrie.trues.api.coverage.league;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.playday.PlaydayFactory;
import de.zahrie.trues.api.coverage.season.PrimeSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import de.zahrie.trues.util.util.Util;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class LeagueLoader extends GamesportsLoader {
  public static League season(String url, String name) {
    int seasonId = Integer.parseInt(Util.between(url, "/prm/", "-"));
    final PrimeSeason season = SeasonFactory.getSeason(seasonId);
    return LeagueFactory.getGroup(season, name);
  }

  public static String divisionNameFromURL(String url) {
    String section = Util.between(url, "/", null, -1);
    section = Util.between(section, "-", null);
    section = section.replace("-", " ");
    if (section.startsWith("division ")) {
      section = Util.replace(section, ".", section.lastIndexOf(" "));
    }
    return Util.capitalize(section);
  }

  private final League league;
  private final String url;

  public LeagueLoader(@NotNull String url) {
    super(URLType.LEAGUE, Integer.parseInt(Util.between(url, "/prm/", "-")), Integer.parseInt(Util.between(url, "/group/", "-")), Integer.parseInt(Util.between(url, "/", "-", -1)));
    final PrimeSeason season = SeasonFactory.getSeason(Integer.parseInt(Util.between(url, "/prm/", "-")));
    this.league = LeagueFactory.getGroup(season, divisionNameFromURL(url));
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
    String leagueName = Util.between(html.find("h1").text(), ":", null);

    if (leagueName.equals(Const.Gamesports.STARTER_NAME)) {
      return List.of();
    }

    final List<LeaguePlayday> playdays = new ArrayList<>();
    List<HTML> findAllByClass = html.findAll("div", "widget-ticker");
    for (int i = 0; i < findAllByClass.size(); i++) {
      final HTML playdayHTML = findAllByClass.get(i);
      final List<PrimeMatch> primeMatches = playdayHTML.findAll("tr").stream()
          .map(match -> match.find("a").getAttribute("href"))
          .map(MatchLoader::idFromURL)
          .map(MatchFactory::getMatch)
          .toList();
      final var playday = new LeaguePlayday(PlaydayFactory.getPlayday(league.getStage(), i + 1), primeMatches);
      playdays.add(playday);
    }
    return playdays;
  }

}
