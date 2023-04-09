package de.zahrie.trues.api.scouting;

import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractScoutingQuery<T> {
  protected final Class<T> clazz;
  protected final ScoutingGameType gameType;
  protected final LocalDateTime start;

  public AbstractScoutingQuery(Class<T> clazz, ScoutingGameType gameType, int days) {
    this.clazz = clazz;
    this.gameType = gameType;
    this.start = LocalDateTime.now().minusDays(days);
  }

  public List<T> performance() {
    return performance("", null);
  }

  public List<T> performance(String selectedColumns) {
    return performance(selectedColumns, null);
  }

  public abstract List<T> performance(String selectedColumns, String suffix);

  public List<T> selection() {
    return selection("", null);
  }

  public List<T> selection(String selectedColumns) {
    return selection(selectedColumns, null);
  }

  public abstract List<T> selection(String selectedColumns, String suffix);
  
  protected abstract String gameTypeString();
}
