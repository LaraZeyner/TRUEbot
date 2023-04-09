package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("app")
public class ApplicationCalendar extends UserCalendar implements Serializable {
  @Serial
  private static final long serialVersionUID = 8384587779953917815L;

  @Column(name = "thread_id")
  private Long threadId;

  public ApplicationCalendar(TimeRange timeRange, String details, DiscordUser discordUser, Long threadId) {
    super(timeRange, details, discordUser);
    this.threadId = threadId;
  }
}
