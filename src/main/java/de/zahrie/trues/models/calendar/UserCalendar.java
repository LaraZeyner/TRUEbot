package de.zahrie.trues.models.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.member.DiscordMember;
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
  private DiscordMember discordMember;

  public UserCalendar(Time start, Time end, String details, UserCalendarType type, DiscordMember discordMember) {
    super(start, end, details);
    this.type = type;
    this.discordMember = discordMember;
  }

  public enum UserCalendarType {
    APPLICATION,
    SCHEDULING
  }

}