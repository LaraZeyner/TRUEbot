package de.zahrie.trues.api.coverage.team;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.league.LeagueLoader;
import de.zahrie.trues.api.coverage.league.model.LeagueBase;
import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.MatchHandler;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.player.model.PRMPlayer;
import de.zahrie.trues.api.coverage.season.signup.SignupFactory;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import de.zahrie.trues.util.io.request.HTML;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod(StringUtils.class)
public class TeamHandler extends TeamModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 1292510240274127687L;

  @SuppressWarnings("unused")
  @Builder
  public TeamHandler(HTML html, String url, PRMTeam team, List<PRMPlayer> players) {
    super(html, url, team, players);
  }

  public void update() {
    final List<HTML> stages = html.findAll("section", "league-team-stage");
    updateResult(stages);
    updateRecordAndSeasons();
    handleStarterMatches(stages);
    team.update();
  }

  public void loadDivision() {
    final LeagueBase currentLeague = Util.avoidNull(team.getCurrentLeague(), null, LeagueTeam::getLeague);
    if (currentLeague instanceof PRMLeague prmLeague) {
      final LeagueLoader leagueLoader = new LeagueLoader(prmLeague.getUrl());
      leagueLoader.load().updateAll();
    }
  }

  private void handleStarterMatches(List<HTML> stages) {
    if (!((PRMLeague) team.getCurrentLeague().getLeague()).isStarter()) return;
    stages.get(stages.size() - 1)
        .find("ul", "league-stage-matches")
        .findAll("li").stream()
        .map(match -> match.find("div").text().between("(", ")") + " -> " +
            Integer.parseInt(match.find("a").getAttribute("href").between("/matches/", "-")))
        .map(Integer::parseInt)
        .map(MatchFactory::getMatch).filter(Objects::nonNull)
        .map(MatchLoader::new).map(MatchLoader::load)
        .forEach(MatchHandler::update);
  }

  private void updateRecordAndSeasons() {
    List<String> teamInfos = html.find("div", "content-portrait-head").findAll("li").stream()
        .map(HTML::text).map(str -> str.after(":")).toList();
    teamInfos = teamInfos.subList(3, teamInfos.size());
    if (teamInfos.size() == 4) {
      final var seasons = Short.parseShort(teamInfos.get(2));
      team.setRecord(teamInfos.get(1), seasons);
    } else if (teamInfos.size() == 2 && teamInfos.get(0).contains("Eingecheckt")) {
      SignupFactory.create(team, teamInfos.get(0));
    }
  }

  private void updateResult(List<HTML> stages) {
    if (stages.isEmpty()) {
      return;
    }
    final String result = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .findAll("li").get(1).text().replace("Ergebnis", "");
    team.setScore(determineDivision(stages), result);
  }

  private PRMLeague determineDivision(List<HTML> stages) {
    final String divisionName = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .find("li").find("a").text();
    final String divisionUrl = stages.get(stages.size() - 1)
        .find("ul", "content-icon-info")
        .find("li").find("a").getAttribute("href");
    return LeagueLoader.season(divisionUrl, divisionName);
  }

}
