package de.zahrie.trues.api.coverage.team;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.league.LeagueLoader;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.MatchHandler;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.util.database.Database;
import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.util.Util;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
@Getter
public class TeamHandler extends TeamModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 1292510240274127687L;

  @Builder
  public TeamHandler(HTML html, String url, PrimeTeam team, List<PrimePlayer> players) {
    super(html, url, team, players);
  }

  public void update() {
    final List<HTML> stages = html.findAll("section", "league-team-stage");
    updateDivision(stages);
    updateResult(stages);
    updateRecordAndSeasons();
    handleStarterMatches(stages);
    Database.save(team);
  }

  public void loadDivision() {
    final LeagueLoader leagueLoader = new LeagueLoader(team.getLeague().getUrl());
    leagueLoader.load().updateAll();
  }

  private void handleStarterMatches(List<HTML> stages) {
    if (!team.getLeague().isStarter()) {
      return;
    }
    stages.get(stages.size() - 1)
        .find("ul", "league-stage-matches")
        .findAll("li").stream()
        .map(match -> Util.between(match.find("div").text(), "(", ")") + " -> " +
            Integer.parseInt(Util.between(match.find("a").getAttribute("href"), "/matches/", "-")))
        .map(Integer::parseInt)
        .map(MatchFactory::getMatch).filter(Objects::nonNull)
        .map(MatchLoader::new).map(MatchLoader::load)
        .forEach(MatchHandler::update);
  }

  private void updateRecordAndSeasons() {
    List<String> teamInfos = html.find("div", "content-portrait-head").findAll("li", "wide").stream()
        .map(HTML::text).map(text -> Util.between(text, ":", null)).toList();
    if (teamInfos.size() == 4) {
      team.setRecord(teamInfos.get(1));
      team.setSeasons(Short.parseShort(teamInfos.get(2)));
    }

  }

  private void updateResult(List<HTML> stages) {
    if (stages.isEmpty()) {
      return;
    }
    final String result = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .findAll("li").get(1).text().replace("Ergebnis", "");
    team.setScore(result);
  }

  private void updateDivision(List<HTML> stages) {
    final String divisionName = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .find("li").find("a").text();
    final String divisionUrl = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .find("li").find("a").getAttribute("href");
    final League league = LeagueLoader.season(divisionUrl, divisionName);
    team.setLeague(league);
  }

}
