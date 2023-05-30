package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "calendar", department = "event")
public class EventCalendar extends EventCalendarBase implements Entity<EventCalendar> {
  @Serial
  private static final long serialVersionUID = -2357919003996341997L;

  public EventCalendar(TimeRange timeRange, String details, Long threadId) {
    super(timeRange, details, threadId);
  }

  private EventCalendar(int id, TimeRange range, String details, Long threadId) {
    super(id, range, details, threadId);
  }

  public static EventCalendar get(List<Object> objects) {
    return new EventCalendar(
        (int) objects.get(0),
        new TimeRange((LocalDateTime) objects.get(2), (LocalDateTime) objects.get(3)),
        (String) objects.get(4),
        (Long) objects.get(6)
    );
  }

  @Override
  public EventCalendar create() {
    return new Query<>(EventCalendar.class)
        .col("calendar_start", range.getStartTime()).col("calendar_end", range.getEndTime()).col("details", details)
        .col("thread_id", threadId).insert(this);
  }
}
