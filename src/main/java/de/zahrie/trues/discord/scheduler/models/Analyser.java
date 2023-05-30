package de.zahrie.trues.discord.scheduler.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.ATournament;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.stage.model.CalibrationStage;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.api.scouting.analyze.RiotPlayerAnalyzer;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.util.io.log.Console;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;

@Schedule
@ExtensionMethod(MatchFactory.class)
public class Analyser extends ScheduledTask {
  @Override
  public void execute() {
    final long start = System.currentTimeMillis();
    RiotPlayerAnalyzer.reset();
    TeamLoader.reset();
    Database.connection().commit(false);
    new Query<>(OrgaTeam.class).entityList().stream().map(OrgaTeam::getTeam).filter(Objects::nonNull)
        .flatMap(team -> team.getPlayers().stream()).forEach(player -> player.loadGames(false));

    PlayerFactory.registeredPlayers().stream().filter(player -> player.getTeam() == null || player.getTeam().getOrgaTeam() == null)
        .forEach(player -> player.loadGames(false, false));
    new Query<>(Player.class, "SELECT player.* FROM player JOIN team t on player.team = t.team_id WHERE refresh >= now()").entityList().forEach(player -> player.loadGames(false));
    Database.connection().commit(true);
    handleNextMatches();

    RiotPlayerAnalyzer.reset();
    new Console("Riot analyse dauerte " + Math.round((System.currentTimeMillis() - start) / 60_000.) + " Minuten.").info();
  }

  private static void handleNextMatches() {
    final List<Integer> upcomingMatches = UpcomingDataFactory.getInstance().getMatches().stream().map(Match::getId).toList();
    for (Match match : getNextOrgaMatches()) {
      if (upcomingMatches.contains(match.getId())) continue;

      if (match instanceof PRMMatch primeMatch) {
        new MatchLoader(primeMatch).load().update();
        Database.connection().commit();
      }

      for (final OrgaTeam orgaTeam : match.getOrgaTeams()) {
        final Participator opponent = match.getOpponent(orgaTeam.getTeam());
        if (opponent == null) continue;

        opponent.getTeamLineup().updateLineups();
      }

      for (Participator participator : match.getParticipators()) {
        participator.getTeamLineup().getLineup().forEach(lineup -> lineup.getPlayer().loadGames(false));
        Database.connection().commit();
      }

      for (OrgaTeam orgaTeam : match.getOrgaTeams()) {
        final Match nextMatch = orgaTeam.getTeam().getMatches().getNextMatch(true);
        if (nextMatch == null) continue;
        if (nextMatch.getId() != match.getId()) continue;

        final Participator opponent = match.getOpponent(orgaTeam.getTeam());
        if (opponent == null) continue;

        ScoutingManager.addForTeam(orgaTeam, opponent, nextMatch);
      }
    }
  }

  @NonNull
  public static List<Match> getNextOrgaMatches() {
    final List<Match> nextMatches = new Query<>(Match.class, "SELECT `_match`.* FROM coverage_team as `_participator` " +
        "INNER JOIN `coverage` as `_match` ON `_participator`.`coverage` = `_match`.`coverage_id` " +
        "INNER JOIN `team` as `_team` ON _participator.team = `_team`.`team_id` " +
        "INNER JOIN `orga_team` as `_orgateam` ON `_team`.`team_id` = `_orgateam`.`team` " +
        "WHERE (_match.result = '-:-') and (_match.coverage_start <= ?) ORDER BY `_match`.`coverage_start` LIMIT 1000")
        .convertList(List.of(LocalDateTime.now().plusHours(326)));
    return nextMatches.stream().filter(match -> !(match instanceof ATournament tM && tM.getLeague().getStage() instanceof CalibrationStage)).toList();
  }


  @Override
  protected String name() {
    return "Riot Analyser";
  }
}
