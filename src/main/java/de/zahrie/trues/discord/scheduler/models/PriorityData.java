package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;

@Schedule
public class PriorityData extends ScheduledTask {
  @Override
  public void execute() {
    for (Match match : UpcomingDataFactory.getInstance().getMatches()) {
      if (match instanceof PRMMatch primeMatch) {
        new MatchLoader(primeMatch).load().update();
        Database.connection().commit();
      }
      for (Participator participator : match.getParticipators()) {
        participator.getTeamLineup().getFixedLineups().forEach(lineup -> lineup.getPlayer().loadGames(false, true));
        Database.connection().commit();
      }
    }
  }

  @Override
  protected String name() {
    return "Priority";
  }
}
