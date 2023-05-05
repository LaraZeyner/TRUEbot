package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "calendar", department = "app")
public class ApplicationCalendar extends UserCalendar implements Entity<ApplicationCalendar>, AThreadable {
  @Serial
  private static final long serialVersionUID = -3831437593024647108L;

  private Long threadId;

  public ApplicationCalendar(TimeRange timeRange, String details, DiscordUser discordUser, Long threadId) {
    super(timeRange, details, discordUser);
    this.threadId = threadId;
  }

  public ApplicationCalendar(int id, TimeRange range, String details, DiscordUser discordUser, Long threadId) {
    super(id, range, details, discordUser);
    this.threadId = threadId;
  }

  public static ApplicationCalendar get(Object[] objects) {
    return new ApplicationCalendar(
        (int) objects[0],
        new TimeRange((LocalDateTime) objects[2], (LocalDateTime) objects[3]),
        (String) objects[4],
        new Query<DiscordUser>().entity(objects[7]),
        (Long) objects[6]
    );
  }

  @Override
  public ApplicationCalendar create() {
    return new Query<ApplicationCalendar>().key("department", "app")
        .col("calendar_start", range.getStartTime())
        .col("calendar_end", range.getEndTime())
        .col("details", details)
        .col("discord_user", user)
        .col("thread_id", threadId).insert(this);
  }
}
