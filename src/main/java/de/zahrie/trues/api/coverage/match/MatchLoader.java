package de.zahrie.trues.api.coverage.match;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.zahrie.trues.api.coverage.GamesportsLoader;
import de.zahrie.trues.api.coverage.league.LeagueFactory;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.PlaydayFactory;
import de.zahrie.trues.api.coverage.playday.PlaydayScheduler;
import de.zahrie.trues.api.coverage.season.PrimeSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.util.Util;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.URLType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 14.02.2023 for TRUEbot
 */
@Getter
public class MatchLoader extends GamesportsLoader {
  public static int idFromURL(String url) {
    return Integer.parseInt(Util.between(url, "/matches/", "-"));
  }

  private PrimeMatch match;

  public MatchLoader(@NotNull PrimeMatch match) {
    super(URLType.MATCH, match.getId());
    this.match = match;
  }

  MatchLoader(int matchId) {
    super(URLType.MATCH, matchId);
  }


  MatchLoader create() {
    final String pageTitle = html.find("h1").text();
    final String seasonName = Util.between(pageTitle, null, ":");
    final PrimeSeason primeSeason = SeasonFactory.getSeason(seasonName);
    final String divisionName = html.find("ul", "breadcrumbs").findAll("li").get(2).text();
    final League league = LeagueFactory.getGroup(primeSeason, divisionName);
    final Playday playday = getPlayday(league);
    final PlaydayScheduler scheduler = new PlaydayScheduler(playday, league);

    this.match = new PrimeMatch(playday, getMatchtime(), league, scheduler.getStart(), scheduler.getEnd(), this.id);
    return this;
  }

  public MatchHandler load() {
    return MatchHandler.builder()
        .html(html)
        .url(url)
        .match(match)
        .teams(getTeams())
        .logs(html.findAll("tr")).build();
  }

  private Playday getPlayday(League league) {
    final List<HTML> data = html.find("div", "content-match-subtitles")
        .findAll("div", "txt-subtitle");
    if (data.size() < 2) {
      return PlaydayFactory.fromMatchtime(league.getStage(), getMatchtime());
    }
    final String playdayName = data.get(1).text();
    final int index = playdayName.equals("Tiebreaker") ? 8 : Integer.parseInt(playdayName.split(" ")[1]);
    return PlaydayFactory.getPlayday(league.getStage(), index);
  }

  private Calendar getMatchtime() {
    final String matchTimeEpoch = html.findId("div", "league-match-time")
        .getAttribute("data-time");
    final Date matchTime = new Date(Long.parseLong(matchTimeEpoch) * 1000);
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(matchTime);
    return calendar;
  }

  private List<PrimeTeam> getTeams() {
    return html.findAll("div", "content-match-head-team-top").stream()
        .map(team -> team.getAttribute("href"))
        .map(TeamLoader::idFromURL)
        .map(TeamFactory::getTeam)
        .toList();
  }

}
