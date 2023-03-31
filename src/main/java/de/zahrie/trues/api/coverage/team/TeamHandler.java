package de.zahrie.trues.api.coverage.team;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.league.LeagueLoader;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.MatchHandler;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.io.request.HTML;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TeamHandler extends TeamModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 1292510240274127687L;

  @SuppressWarnings("unused")
  @Builder
  public TeamHandler(HTML html, String url, PrimeTeam team, List<PrimePlayer> players) {
    super(html, url, team, players);
  }

  public void update() {
    final List<HTML> stages = html.findAll("section", "league-team-stage");
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
        .map(match -> match.find("div").text().between("(", ")") + " -> " +
            Integer.parseInt(match.find("a").getAttribute("href").between("/matches/", "-").toString()))
        .map(Integer::parseInt)
        .map(MatchFactory::getMatch).filter(Objects::nonNull)
        .map(MatchLoader::new).map(MatchLoader::load)
        .forEach(MatchHandler::update);
  }

  private void updateRecordAndSeasons() {
    final List<String> teamInfos = html.find("div", "content-portrait-head").findAll("li", "wide").stream()
        .map(HTML::text).map(text -> text.between(":").toString()).toList();
    if (teamInfos.size() == 4) {
      final var seasons = Short.parseShort(teamInfos.get(2));
      team.setRecord(teamInfos.get(1), seasons);
    }
  }

  private void updateResult(List<HTML> stages) {
    if (stages.isEmpty()) {
      return;
    }
    final Chain result = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .findAll("li").get(1).text().replace("Ergebnis", "");
    team.setScore(determineDivision(stages), result.toString());
  }

  private League determineDivision(List<HTML> stages) {
    final Chain divisionName = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .find("li").find("a").text();
    final Chain divisionUrl = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .find("li").find("a").getAttribute("href");
    return LeagueLoader.season(divisionUrl, divisionName);
  }

}
