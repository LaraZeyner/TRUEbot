package de.zahrie.trues.api.coverage.league;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.playday.PlaydayFactory;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Const;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@ExtensionMethod(StringUtils.class)
public class LeagueLoader extends GamesportsLoader {
  public static League season(String url, String name) {
    final int seasonId = Integer.parseInt(url.between("/prm/", "-"));
    final int stageId = Integer.parseInt(url.between("/group/", "-"));
    final PRMSeason season = SeasonFactory.getSeason(seasonId);
    return LeagueFactory.getGroup(season, name, stageId);
  }

  public static String divisionNameFromURL(String url) {
    String section = url.between("/", null, -1).between("-").replace("-", " ");
    if (section.startsWith("division ")) section = section.replaces(".", section.lastIndexOf(" "));
    return section.capitalizeFirst();
  }

  public static Integer stageIdFromUrl(String url) {
    return url.between("/group/", "-").intValue();
  }

  private final PRMLeague league;
  private final String url;

  public LeagueLoader(@NotNull String url) {
    super(URLType.LEAGUE, url.between("/prm/", "-").intValue(), url.between("/group/", "-").intValue(),
        url.between("/", "-", -1).intValue());
    final PRMSeason season = SeasonFactory.getSeason(url.between("/prm/", "-").intValue());
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
  private List<PRMTeam> getTeams() {
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
    final String leagueName = html.find("h1").text().between(":");

    if (leagueName.equals(Const.Gamesports.STARTER_NAME)) {
      return List.of();
    }

    final List<LeaguePlayday> playdays = new ArrayList<>();
    final List<HTML> findAllByClass = html.findAll("div", "widget-ticker");
    for (int i = 0; i < findAllByClass.size(); i++) {
      final HTML playdayHTML = findAllByClass.get(i);
      final List<PRMMatch> primeMatches = playdayHTML.findAll("tr").stream()
          .map(match -> match.find("a").getAttribute("href"))
          .map(MatchLoader::idFromURL)
          .map(MatchFactory::getMatch)
          .filter(Objects::nonNull)
          .toList();
      final var playday = new LeaguePlayday(PlaydayFactory.getPlayday(league.getStage(), i + 1), primeMatches);
      playdays.add(playday);
    }
    return playdays;
  }

}
