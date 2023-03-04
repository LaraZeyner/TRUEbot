package de.zahrie.trues.api.datatypes.calendar;

import de.zahrie.trues.api.coverage.playday.config.AbstractTimeRange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Lara on 28.02.2023 for TRUEbot
 */
@RequiredArgsConstructor
@Getter
public class TimeRange implements AbstractTimeRange {
  private final Time start;
  private final Time end;


  @Override
  public Time start() {
    return start;
  }

  @Override
  public Time end() {
    return end;
  }
}
