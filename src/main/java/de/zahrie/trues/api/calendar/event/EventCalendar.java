package de.zahrie.trues.api.calendar.event;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.calendar.UserCalendar;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.Setter;

/**
 * kurze Events
 */
@Getter
@Setter
@Table(value = "calendar", department = "event")
public class EventCalendar extends UserCalendar implements Entity<EventCalendar> {
  @Serial
  private static final long serialVersionUID = -2357919003996341997L;
  private Long threadId;

  public void setThreadId(long threadId) {
    if (this.threadId.equals(threadId)) return;
    new Query<>(EventCalendar.class).col("thread_id", threadId).update(id);
    this.threadId = threadId;
  }

  public EventCalendar(TimeRange timeRange, String details, DiscordUser creator) {
    super(timeRange, details, creator);
  }

  private EventCalendar(int id, TimeRange range, String details, DiscordUser creator, long threadId) {
    super(id, range, details, creator);
    this.threadId = threadId;
  }

  public static EventCalendar get(List<Object> objects) {
    return new EventCalendar(
        (int) objects.get(0),
        new TimeRange((LocalDateTime) objects.get(2), (LocalDateTime) objects.get(3)),
        (String) objects.get(4),
        new Query<>(DiscordUser.class).entity(objects.get(7)),
        (Long) objects.get(6)
    );
  }

  @Override
  public EventCalendar create() {
    return new Query<>(EventCalendar.class)
        .col("calendar_start", range.getStartTime()).col("calendar_end", range.getEndTime()).col("details", details)
        .col("discord_user", user.getId()).col("thread_id", threadId)
        .insert(this);
  }

  private Event event;

  public Event getEvent() {
    if (event == null) this.event = new Query<>(Event.class).where("calendar", id).entity();
    return event;
  }


}
