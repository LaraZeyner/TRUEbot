package de.zahrie.trues.discord.scheduler.models;

import java.util.List;

import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.model.LoaderGameType;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.clash.ClashLoader;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;

@Schedule
public class PriorityData extends ScheduledTask {
  @Override
  public void execute() {
    UpcomingDataFactory.refresh();
    for (Match match : UpcomingDataFactory.getInstance().getMatches()) {
      if (match instanceof PRMMatch primeMatch) {
        new MatchLoader(primeMatch).load().update();
        Database.connection().commit();
      }
      for (Participator participator : match.getParticipators()) {
        participator.getTeamLineup().getFixedLineups().forEach(lineup -> lineup.getPlayer().forceLoadMatchmade());
        Database.connection().commit();
      }
    }

    if (ClashLoader.isClashActive()) {
      final Season currentSeason = SeasonFactory.getCurrentSeason();
      if (currentSeason == null) return;

      new Query<>(Player.class, "SELECT _player.* FROM league_team _leagueteam JOIN player _player on _leagueteam.team = _player.team WHERE league IN ((SELECT league FROM league_team _leagueteam JOIN orga_team _orgateam on _leagueteam.team = _orgateam.team JOIN coverage_group _league on _leagueteam.league = _league.coverage_group_id JOIN coverage_stage _stage on _league.stage = _stage.coverage_stage_id WHERE _stage.season = ?))").entityList(List.of(currentSeason)).forEach(player -> player.loadGames(LoaderGameType.CLASH));
    }
  }

  @Override
  protected String name() {
    return "Priority";
  }
}
