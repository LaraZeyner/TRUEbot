package de.zahrie.trues.discord.scheduler;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.discord.scheduler.models.Analyser;
import de.zahrie.trues.discord.scheduler.models.BotRestarter;
import de.zahrie.trues.discord.scheduler.models.CheckSubstitudeStatus;
import de.zahrie.trues.discord.scheduler.models.LeaderboardUpdater;
import de.zahrie.trues.discord.scheduler.models.PriorityData;
import de.zahrie.trues.discord.scheduler.models.TeamInfoUpdater;

public class ScheduleRegisterer implements Registerer<List<ScheduledTask>> {
  @Override
  public List<ScheduledTask> register() {
    return List.of(
        new Analyser(),
        new BotRestarter(),
        new CheckSubstitudeStatus(),
        new LeaderboardUpdater(),
        new PriorityData(),
        new TeamInfoUpdater()
    );
  }
}
