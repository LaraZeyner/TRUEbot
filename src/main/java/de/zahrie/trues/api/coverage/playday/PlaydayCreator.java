package de.zahrie.trues.api.coverage.playday;

import de.zahrie.trues.api.coverage.league.model.LeagueTier;
import de.zahrie.trues.api.coverage.playday.scheduler.PlaydayScheduler;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaydayCreator {
  private final Stage stage;
  private final int index;
  private final LeagueTier tier;

  public PlaydayCreator(Stage stage, int index) {
    this.stage = stage;
    this.index = index;
    this.tier = LeagueTier.Division_3;
  }

  public Playday create() {
    final PlaydayScheduler scheduler = PlaydayScheduler.create(stage, index, tier);
    return new Playday(stage, (short) index, scheduler.playday(), ((PlayStage) stage).playdayConfig().format()).create();
  }

}
