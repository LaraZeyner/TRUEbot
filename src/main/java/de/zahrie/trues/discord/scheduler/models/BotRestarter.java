package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.LoadupManager;
import de.zahrie.trues.api.scouting.AnalyzeManager;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.util.io.log.Console;

@Schedule(hour = "6", minute = "0")
public class BotRestarter extends ScheduledTask {
  @Override
  public void execute() {
    LoadupManager.getInstance().restart();
    new Console("Bot neu gestartet.").info();
    AnalyzeManager.reset();
  }
}
