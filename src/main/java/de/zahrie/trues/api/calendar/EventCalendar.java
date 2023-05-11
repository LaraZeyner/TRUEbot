package de.zahrie.trues.api.calendar;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("calendar")
public abstract class EventCalendar extends Calendar implements AThreadable {
  protected Long threadId; // thread_id

  public EventCalendar(TimeRange timeRange, String details, Long threadId) {
    super(timeRange, details);
    this.threadId = threadId;
  }

  protected EventCalendar(int id, TimeRange range, String details, Long threadId) {
    super(id, range, details);
    this.threadId = threadId;
  }
}
