package de.zahrie.trues.discord.scheduler.models;

import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.team.TeamHandler;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.discord.scouting.teaminfo.TeamInfoManager;
import de.zahrie.trues.util.io.log.Console;
import lombok.experimental.ExtensionMethod;

@Schedule
@ExtensionMethod(MatchFactory.class)
public class EmbedData extends ScheduledTask {
  @Override
  public void execute() {
    final long start = System.currentTimeMillis();
    UpcomingDataFactory.refresh();
    loadPRMData();
    new Console("Discord analyse dauerte " + Math.round((System.currentTimeMillis() - start) / 60_000.) + " Minuten.").info();
  }

  private static void loadPRMData() {
    final List<OrgaTeam> orgaTeams = new Query<>(OrgaTeam.class).entityList();
    for (OrgaTeam orgaTeam : orgaTeams) {
      final Team team = orgaTeam.getTeam();
      if (team == null) continue;

      Database.connection().commit(false);
      handlePRMData(team);
      Database.connection().commit(true);
      TeamInfoManager.fromTeam(orgaTeam).updateAll();
    }
  }

  private static void handlePRMData(Team team) {
    final PRMTeam prmTeam = new Query<>(PRMTeam.class).entity(team.getId());
    if (prmTeam == null) return;

    final TeamLoader teamLoader = new TeamLoader(prmTeam);
    final TeamHandler teamHandler = teamLoader.load();
    if (teamHandler == null) return;

    final League division = teamHandler.loadDivision();
    if (division == null) return;

    division.getLeagueTeams().stream().map(LeagueTeam::getTeam)
        .filter(t -> t.getOrgaTeam() == null)
        .flatMap(t -> t.getPlayers().stream())
        .forEach(player -> player.loadGames(true));
  }

  @Override
  protected String name() {
    return "Discord Analyser";
  }
}
