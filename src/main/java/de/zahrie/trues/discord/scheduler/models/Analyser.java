package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.lineup.LineupFinder;
import de.zahrie.trues.api.coverage.lineup.LineupManager;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.team.TeamHandler;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.analyze.PlayerAnalyzer;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.discord.scouting.teaminfo.TeamInfoManager;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Schedule
@Log
@ExtensionMethod(MatchFactory.class)
public class Analyser extends ScheduledTask {
  @Override
  public void execute() {
    final long start = System.currentTimeMillis();
    UpcomingDataFactory.refresh();
    loadPRMData();
    Database.connection().commit(false);
    MatchFactory.getNextTeams().forEach(team -> team.getPlayers().forEach(player -> new PlayerAnalyzer(player).analyze()));
    PlayerFactory.registeredPlayers().forEach(player -> new PlayerAnalyzer(player).analyze());
    Database.connection().commit(true);
    log.info("Analyse dauerte " + ((System.currentTimeMillis() - start) / 60_000) + " Minuten.");
  }

  private static void loadPRMData() {
    for (OrgaTeam orgaTeam : new Query<OrgaTeam>().entityList()) {
      final TeamBase team = orgaTeam.getTeam();
      if (team == null) continue;

      final PRMTeam prmTeam = new Query<PRMTeam>().entity(team.getId());
      if (prmTeam != null) {
        Database.connection().commit(false);
        final TeamLoader teamLoader = new TeamLoader(prmTeam);
        final TeamHandler teamHandler = teamLoader.load();
        teamHandler.loadDivision();
        Database.connection().commit(true);
      }

      final Match nextMatch = team.getMatches().getNextMatch(true);
      if (nextMatch != null) {
        final Participator opponent = nextMatch.getOpponent(team);
        ScoutingManager.addForTeam(orgaTeam, opponent, nextMatch);
        LineupManager.getMatch(nextMatch);
        LineupFinder.update(opponent);
      }
      TeamInfoManager.fromTeam(orgaTeam).updateAll();
    }
  }
}
