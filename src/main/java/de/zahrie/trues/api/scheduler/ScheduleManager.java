package de.zahrie.trues.api.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.zahrie.trues.discord.scheduler.ScheduleRegisterer;

public class ScheduleManager {
  public static Map<String, String> lastLines = new HashMap<>();
  private static List<ScheduledTask> tasks = new ScheduleRegisterer().register();

  public static void reset() {
    tasks = new ScheduleRegisterer().register();
  }

  public static void run() {
    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        tasks.forEach(ScheduledTask::start);
      }
    }, 0, 60_000L);
  }
}
