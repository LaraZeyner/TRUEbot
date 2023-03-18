package de.zahrie.trues.models.community.application;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.models.calendar.UserCalendar;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "Application")
@Table(name = "application", indexes = {@Index(name = "idx_app", columnList = "discord_user, lineup_role, lane", unique = true)})
@NamedQuery(name = "Application.current", query = "SELECT member.mention, role || ' - ' || position, isWaiting FROM Application WHERE isWaiting is not null")
public class Application implements Serializable {
  @Serial
  private static final long serialVersionUID = -6006729315935528279L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "application_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "discord_user", nullable = false)
  @ToString.Exclude
  private DiscordMember member;

  @Enumerated(EnumType.STRING)
  @Column(name = "lineup_role", nullable = false, length = 6)
  private TeamRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "lane", length = 15)
  private TeamPosition position;

  @Type(TimeCoverter.class)
  @Column(name = "app_timestamp")
  private Time appTimestamp = new Time();

  @Column(name = "app_accepted")
  private Boolean isWaiting = true;

  @Column(name = "app_notes", length = 2048)
  private String appNotes;

  public Application(DiscordMember member, TeamRole role, TeamPosition position, String appNotes) {
    this.member = member;
    this.role = role;
    this.position = position;
    this.appNotes = appNotes;
  }

  public void schedule(Time time) {
    // TODO (Abgie) 15.03.2023: never used
    isWaiting = false;
    final UserCalendar calendar = new UserCalendar(time, time.plus(Time.MINUTE, 30), appNotes, UserCalendar.UserCalendarType.APPLICATION, member);
    Database.save(calendar);
  }
}
