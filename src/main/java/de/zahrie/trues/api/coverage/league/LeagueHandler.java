package de.zahrie.trues.api.coverage.league;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.zahrie.trues.api.coverage.match.MatchHandler;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.team.TeamHandler;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.database.Database;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
@Getter
public class LeagueHandler extends LeagueModel implements Serializable {
  @Serial
  private static final long serialVersionUID = -5698606611490610348L;

  @Builder
      //TODO (Abgie) 01.03.2023: Never used
  LeagueHandler(String url, League league, List<PrimeTeam> teams, List<LeaguePlayday> playdays) {
    super(url, league, teams, playdays);
  }

  public void updateAll() {
    updateTeams();
    updateMatches();
    Database.save(league);
  }

  public void updateMatches() {
    if (league.isStarter()) {
      return;
    }
    playdays.stream().flatMap(playday -> playday.matches().stream())
        .map(MatchLoader::new)
        .map(MatchLoader::load)
        .forEach(MatchHandler::update);
  }

  public void updateTeams() {
    teams.stream().map(TeamLoader::new)
        .map(TeamLoader::load)
        .forEach(TeamHandler::update);
  }
}
