package de.zahrie.trues.api.coverage.playday;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.playday.scheduler.PlaydayScheduleHandler;
import de.zahrie.trues.api.coverage.playday.scheduler.PlaydayScheduler;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.util.database.Database;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaydayCreator {
  private final PlayStage stage;
  private final int index;
  private final LeagueTier tier;

  public PlaydayCreator(PlayStage stage, int index) {
    this.stage = stage;
    this.index = index;
    this.tier = LeagueTier.Division_3;
  }

  public Playday create() {
    final PlaydayScheduler scheduler = new PlaydayScheduleHandler(stage, index, tier).create();
    Playday playday = new Playday(stage, (short) index, scheduler.playday().start(), scheduler.playday().end());
    Database.save(playday);
    return playday;
  }

}
