package de.zahrie.trues.api.calendar.scheduling;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.calendar.UserCalendar;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "calendar", department = "schedule")
public class SchedulingCalendar extends UserCalendar implements Entity<SchedulingCalendar> {
  @Serial
  private static final long serialVersionUID = -3658276021282430693L;


  public SchedulingCalendar(TimeRange timeRange, String details, DiscordUser discordUser) {
    super(timeRange, details, discordUser);
  }

  private SchedulingCalendar(int id, TimeRange range, String details, DiscordUser discordUser) {
    super(id, range, details, discordUser);
  }

  public static SchedulingCalendar get(Object[] objects) {
    return new SchedulingCalendar(
        (int) objects[0],
        new TimeRange((LocalDateTime) objects[2], (LocalDateTime) objects[3]),
        (String) objects[4],
        new Query<DiscordUser>().entity( objects[7])
    );
  }

  @Override
  public SchedulingCalendar create() {
    return new Query<SchedulingCalendar>().key("department", "app")
        .col("calendar_start", range.getStartTime())
        .col("calendar_end", range.getEndTime())
        .col("details", details)
        .col("discord_user", user)
        .insert(this);
  }
}