package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.api.coverage.match.MatchFactory;
import de.zahrie.trues.api.discord.builder.leaderboard.LeaderboardHandler;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.util.io.log.Console;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Schedule(minute = "0")
@Log
@ExtensionMethod(MatchFactory.class)
public class LeaderboardUpdater extends ScheduledTask {
  @Override
  public void execute() {
    LeaderboardHandler.handleLeaderboards();
    new Console("Leaderboard aktualisiert.").info();
  }
}
