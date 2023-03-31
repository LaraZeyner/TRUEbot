package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.user.DiscordUser;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "calendar_type", nullable = false, length = 11)
  private UserCalendarType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "discord_user")
  @ToString.Exclude
  private DiscordUser discordUser;

  public UserCalendar(Time start, Time end, String details, UserCalendarType type, DiscordUser discordUser) {
    super(start, end, details);
    this.type = type;
    this.discordUser = discordUser;
  }

  public enum UserCalendarType {
    APPLICATION,
    SCHEDULING
  }

}