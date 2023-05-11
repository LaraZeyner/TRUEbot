package de.zahrie.trues.api.scheduler;

import de.zahrie.trues.util.io.log.Console;

public abstract class ScheduledTask {
  private final Schedule schedule;
  private boolean isRunning;

  public ScheduledTask() {
    this.schedule = getClass().asSubclass(this.getClass()).getAnnotation(Schedule.class);
    this.isRunning = false;
  }

  public abstract void execute();

  public void handleTask() {
    if (!isRunning && new ScheduleComparer(schedule).test()) {
      this.isRunning = true;
      new Console(getClass().getSimpleName()).entering();
      execute();
      new Console(getClass().getSimpleName()).exiting();
      this.isRunning = false;
    }
  }
}
