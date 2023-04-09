package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("user")
public class UserCalendar extends CalendarBase implements Serializable {
  @Serial
  private static final long serialVersionUID = 8384587779953917815L;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "discord_user")
  @ToString.Exclude
  private DiscordUser user;

  public UserCalendar(TimeRange timeRange, String details, DiscordUser user) {
    super(timeRange, details);
    this.user = user;
  }
}