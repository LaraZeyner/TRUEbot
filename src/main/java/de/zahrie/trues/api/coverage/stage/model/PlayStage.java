package de.zahrie.trues.api.coverage.stage.model;

import java.util.List;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.stage.IdAble;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;

public interface PlayStage extends IdAble, Playable, Id {
  default List<League> leagues() {
    return new Query<>(League.class).where("stage", this).entityList();
  }
}
