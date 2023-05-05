package de.zahrie.trues.api.calendar;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;

public interface ACalendar {
  TimeRange getRange();
  void setRange(TimeRange range);
  String getDetails();
}
