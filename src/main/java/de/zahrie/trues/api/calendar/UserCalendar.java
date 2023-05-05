package de.zahrie.trues.api.calendar;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("calendar")
public abstract class UserCalendar extends Calendar {
  protected DiscordUser user; // discord_user

  public UserCalendar(TimeRange timeRange, String details, DiscordUser user) {
    super(timeRange, details);
    this.user = user;
  }

  protected UserCalendar(int id, TimeRange range, String details, DiscordUser user) {
    super(id, range, details);
    this.user = user;
  }
}