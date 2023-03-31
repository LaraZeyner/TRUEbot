package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.LoadupManager;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;

@Schedule(hour = "6", minute = "0")
public class BotRestarter extends ScheduledTask {
  @Override
  public void execute() {
    LoadupManager.getInstance().restart();
  }
}
