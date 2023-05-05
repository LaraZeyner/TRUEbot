package de.zahrie.trues.util.io.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.zahrie.trues.api.discord.user.DiscordUser;

public abstract class Log {

  protected String getMessage(Level level, String msg) {
    return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " [" + level.name() + "] " + msg;
  }

  public abstract void doCommand(DiscordUser user, String command, String full);
  protected abstract void doLog(Level level, String msg);

  public String severe(String msg) {
    doLog(Level.DEBUG, msg);
    return msg;
  }

  public String severe(Throwable throwable) {
    return severe(null, throwable);
  }

  public String severe(String title, Throwable throwable) {
    return doThrow(title, throwable, Level.SEVERE);
  }

  public String error(String title, Throwable throwable) {
    return doThrow(title, throwable, Level.ERROR);
  }

  public String error(String msg) {
    doLog(Level.ERROR, msg);
    return msg;
  }

  public String error(Throwable throwable) {
    return error(null, throwable);
  }

  public String warn(String title, Throwable throwable) {
    return doThrow(title, throwable, Level.WARNING);
  }

  public String warn(String msg) {
    doLog(Level.WARNING, msg);
    return msg;
  }

  public String warn(Throwable throwable) {
    return warn(null, throwable);
  }

  public String debug(String msg) {
    doLog(Level.DEBUG, msg);
    return msg;
  }

  public String debug(Throwable throwable) {
    return debug(null, throwable);
  }

  public String debug(String title, Throwable throwable) {
    return doThrow(title, throwable, Level.DEBUG);
  }

  public String config(String title, Throwable throwable) {
    return doThrow(title, throwable, Level.CONFIG);
  }

  public String config(String msg) {
    doLog(Level.CONFIG, msg);
    return msg;
  }

  public String config(Throwable throwable) {
    return config(null, throwable);
  }

  public String entering(String msg) {
    return debug("BEGINN VON (" + msg + ")");
  }

  public String exiting(String msg) {
    return debug("ENDE VON (" + msg + ")");
  }

  public String info(String title, Throwable throwable) {
    return doThrow(title, throwable, Level.INFO);
  }

  public String info(String msg) {
    doLog(Level.INFO, msg);
    return msg;
  }

  public String info(Throwable throwable) {
    return info(null, throwable);
  }

  private String doThrow(String title, Throwable throwable, Level level) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    final String printTitle = title == null ? "" : title + "\n";
    doLog(level, printTitle + sw);
    return sw.toString();
  }
}
