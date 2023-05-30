package de.zahrie.trues.api.scheduler;

public abstract class ScheduledTask {
  private final Schedule schedule;
  private Thread thread = null;

  public ScheduledTask() {
    this.schedule = getClass().asSubclass(this.getClass()).getAnnotation(Schedule.class);
  }

  public abstract void execute() throws InterruptedException;

  protected abstract String name();

  private boolean notValid() {
    return !new ScheduleComparer(schedule).test();
  }

  public void start() {
    if (thread != null && thread.isAlive() || notValid()) return;
    this.thread = new Thread(null, this::run, name());
    thread.start();
  }

  public void run() {
    try {
      execute();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
