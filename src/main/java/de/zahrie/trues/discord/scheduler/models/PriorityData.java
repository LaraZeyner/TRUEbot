package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.api.coverage.match.MatchLoader;
import de.zahrie.trues.api.coverage.match.UpcomingDataFactory;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.riot.analyze.PlayerAnalyzer;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.database.Database;

@Schedule
public class PriorityData extends ScheduledTask {
  @Override
  public void execute() {
    for (Match match : UpcomingDataFactory.getInstance().getMatches()) {
      if (match instanceof PrimeMatch primeMatch) {
        new MatchLoader(primeMatch).load().update();
        Database.connection().commit();
      }
      for (Participator participator : match.getParticipators()) {
        participator.getLineups().forEach(lineup -> new PlayerAnalyzer(lineup.getPlayer()).analyze());
        Database.connection().commit();
      }
    }
  }
}
