package de.zahrie.trues.api.calendar.scheduling;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.calendar.UserCalendar;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("schedule")
public class SchedulingCalendar extends UserCalendar implements Serializable {
  @Serial
  private static final long serialVersionUID = 8384587779953917815L;

  public SchedulingCalendar(TimeRange timeRange, String details, DiscordUser discordUser) {
    super(timeRange, details, discordUser);
  }
}
