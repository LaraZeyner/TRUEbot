package de.zahrie.trues.discord.scheduler.models;

import de.zahrie.trues.LoadupManager;
import de.zahrie.trues.api.coverage.team.TeamHandler;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.riot.clash.ClashLoader;
import de.zahrie.trues.api.scheduler.Schedule;
import de.zahrie.trues.api.scheduler.ScheduleManager;
import de.zahrie.trues.api.scheduler.ScheduledTask;
import de.zahrie.trues.util.io.cfg.JSON;
import de.zahrie.trues.util.io.log.Console;

@Schedule(hour = "6", minute = "0")
public class BotRestarter extends ScheduledTask {
  @Override
  public void execute() {
    final String collect = String.join("\n", ScheduleManager.lastLines.values());
    JSON.write("thread-performance.txt", "NEUSTART AT " + TimeFormat.SYSTEM.now() + "\n" +
        collect + "\n=======================================");

    for (PRMTeam team : new Query<>(PRMTeam.class, "SELECT * FROM team WHERE highlight = true").entityList()) {
      final TeamLoader teamLoader = new TeamLoader(team);
      final TeamHandler load = teamLoader.load();
      if (load != null) load.update(true);
    }

    ClashLoader.loadAllClashes();

    LoadupManager.getInstance().restart();
    new Console("Bot neu gestartet.").info();
  }

  @Override
  protected String name() {
    return "Restarter";
  }
}
