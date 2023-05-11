package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.scouting.analyze.RiotPlayerAnalyzer;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.discord.scouting.teaminfo.TeamInfoManager;
import de.zahrie.trues.util.io.log.Console;
import lombok.experimental.ExtensionMethod;

@Schedule
@ExtensionMethod(MatchFactory.class)
public class Analyser extends ScheduledTask {
  @Override
  public void execute() {
    final long start = System.currentTimeMillis();
    UpcomingDataFactory.refresh();
    loadPRMData();
    Database.connection().commit(false);
    MatchFactory.getNextTeams().forEach(team -> team.getPlayers().forEach(player -> new RiotPlayerAnalyzer(player).analyze()));
    PlayerFactory.registeredPlayers().forEach(player -> new RiotPlayerAnalyzer(player).analyze());
    Database.connection().commit(true);
    new Console("Analyse dauerte " + ((System.currentTimeMillis() - start) / 60_000) + " Minuten.").info();
  }

  private static void loadPRMData() {
    for (OrgaTeam orgaTeam : new Query<>(OrgaTeam.class).entityList()) {
      new Console(orgaTeam.getName()).entering();
      final Team team = orgaTeam.getTeam();
      if (team == null) continue;

      /*
      final PRMTeam prmTeam = new Query<>(PRMTeam.class).entity(team.getId());
      if (prmTeam != null) {
        Database.connection().commit(false);
        final TeamLoader teamLoader = new TeamLoader(prmTeam);
        final TeamHandler teamHandler = teamLoader.load();
        if (teamHandler != null) teamHandler.loadDivision();
        Database.connection().commit(true);
      }
      
       */

      final Match nextMatch = team.getMatches().getNextMatch(true);
      if (nextMatch != null) {
        final Participator opponent = nextMatch.getOpponent(team);
        ScoutingManager.addForTeam(orgaTeam, opponent, nextMatch);
        opponent.getTeamLineup().updateLineups();
      }
      TeamInfoManager.fromTeam(orgaTeam).updateAll();
      new Console(orgaTeam.getName()).exiting();
    }
  }
}
