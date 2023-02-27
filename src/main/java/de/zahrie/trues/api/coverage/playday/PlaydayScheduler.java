package de.zahrie.trues.api.coverage.playday;

import java.util.Calendar;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.util.Const;
import lombok.Getter;

/**
 * Created by Lara on 16.02.2023 for TRUEbot
 */
@Getter
public final class PlaydayScheduler {
  private final Calendar start;
  private final Calendar end;
  private final Calendar alternative;

  public PlaydayScheduler(Playday playday, League league) {
    this.start = playday.getStart();
    if (league.getName().equals(Const.Gamesports.STARTER_NAME)) {
      start.add(Calendar.DATE, 1);
    }

    this.end = playday.getEnd();
    if (!league.getName().equals(Const.Gamesports.STARTER_NAME)) {
      end.add(Calendar.DATE, 7);
    }

    this.alternative = league.getAlternative(playday);
  }
}
