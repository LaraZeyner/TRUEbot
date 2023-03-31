package de.zahrie.trues.discord.scheduler.models;

import java.util.Calendar;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamHandler;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.riot.analyze.PlayerAnalyzer;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Schedule
@Log
@ExtensionMethod(MatchFactory.class)
public class Analyser extends ScheduledTask {
  @Override
  public void execute() {
    final Calendar start = Calendar.getInstance();
    UpcomingDataFactory.refresh();
    loadPRMData();
    Database.connection().commit(false);
    MatchFactory.getNextTeams().forEach(team -> team.getPlayers().forEach(player -> new PlayerAnalyzer(player).analyze()));
    PlayerFactory.registeredPlayers().forEach(player -> new PlayerAnalyzer(player).analyze());
    Database.connection().commit(true);
    log.info("Analyse dauerte " + ((Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()) / 60_000) + " Minuten.");
  }

  private static void loadPRMData() {
    for (OrgaTeam orgaTeam : Database.Find.findList(OrgaTeam.class)) {
      final Team team = orgaTeam.getTeam();
      if (team != null && ((PrimeTeam) team).getPrmId() != null) {
        Database.connection().commit(false);
        final Integer teamId = ((PrimeTeam) team).getPrmId();
        final PrimeTeam primeTeam = TeamFactory.getTeam(teamId);
        final TeamLoader teamLoader = new TeamLoader(primeTeam);
        final TeamHandler teamHandler = teamLoader.load();
        teamHandler.loadDivision();
        Database.connection().commit(true);
      }
      final Match nextMatch = team.getNextMatch();
      final Participator opponent = nextMatch.getOpponent(team);
      if (team == null) continue;
      ScoutingManager.addForTeam(orgaTeam, opponent, nextMatch);
    }
  }
}
