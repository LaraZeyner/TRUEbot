package de.zahrie.trues.api.coverage.stage.model;

import java.util.List;

import de.zahrie.trues.api.coverage.league.model.AbstractLeague;
import de.zahrie.trues.api.coverage.stage.IdAble;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;

public interface PlayStage extends IdAble, Playable, Id {
  default List<AbstractLeague> leagues() {
    return new Query<>(AbstractLeague.class).where("stage", this).entityList();
  }
}
