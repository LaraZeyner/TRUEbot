package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.AMatch;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.riot.analyze.PlayerAnalyzer;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.api.database.connector.Database;

@Schedule
public class PriorityData extends ScheduledTask {
  @Override
  public void execute() {
    for (AMatch AMatch : UpcomingDataFactory.getInstance().getMatches()) {
      if (AMatch instanceof PRMMatch primeMatch) {
        new MatchLoader(primeMatch).load().update();
        Database.connection().commit();
      }
      for (Participator participator : AMatch.getParticipators()) {
        participator.getLineups().forEach(lineup -> new PlayerAnalyzer(lineup.getPlayer()).analyze());
        Database.connection().commit();
      }
    }
  }
}
