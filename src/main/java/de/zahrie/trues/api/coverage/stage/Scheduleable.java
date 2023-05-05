package de.zahrie.trues.api.coverage.stage;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;

public interface Scheduleable {
  TimeRange getRange(); // scheduling_start, scheduling_end
  void setRange(TimeRange range);
  boolean isScheduleable();
}
