package de.zahrie.trues.api.coverage.playday.config;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.Getter;

/**
 * Created by Lara on 28.02.2023 for TRUEbot
 */
@Getter
public class PlaydayRange extends TimeRange {
  public PlaydayRange(Time start, Time end) {
    super(start, end);
  }
}
