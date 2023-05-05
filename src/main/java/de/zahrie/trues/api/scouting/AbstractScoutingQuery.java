package de.zahrie.trues.api.scouting;

import java.time.LocalDateTime;

import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.matchhistory.game.Selection;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;

public abstract class AbstractScoutingQuery<T> {
  protected final Class<T> clazz;
  protected final ScoutingGameType gameType;
  protected final LocalDateTime start;

  public AbstractScoutingQuery(Class<T> clazz, ScoutingGameType gameType, int days) {
    this.clazz = clazz;
    this.gameType = gameType;
    this.start = LocalDateTime.now().minusDays(days);
  }

  public abstract Query<Performance> performance();

  public abstract Query<Selection> selection();
  
  protected abstract Query<Performance> gameTypeString(Query<Performance> query);
}
