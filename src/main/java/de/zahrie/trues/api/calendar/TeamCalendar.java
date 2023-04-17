package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.DTO;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.discord.notify.NotificationManager;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
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
import lombok.experimental.ExtensionMethod;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("team")
@ExtensionMethod(StringUtils.class)
public class TeamCalendar extends CalendarBase implements Serializable, DTO {
  @Serial
  private static final long serialVersionUID = 8384587779953917815L;

  @Enumerated(EnumType.STRING)
  @Column(name = "calendar_type", nullable = false, length = 11)
  private TeamCalendarType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "orga_team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Column(name = "thread_id")
  private Long threadId;

  public static TeamCalendar create(TimeRange timeRange, String details, TeamCalendarType type, OrgaTeam orgaTeam, long threadId) {
    final TeamCalendar calendar = new TeamCalendar(timeRange, details, type, orgaTeam, threadId);
    if (timeRange.getStartTime().isBefore(LocalDateTime.now().plusDays(1))) {
      NotificationManager.addNotifiersFor(calendar);
    }
    Database.insert(calendar);
    return calendar;
  }

  private TeamCalendar(TimeRange timeRange, String details, TeamCalendarType type, OrgaTeam orgaTeam, long threadId) {
    super(timeRange, details);
    this.type = type;
    this.orgaTeam = orgaTeam;
    this.threadId = threadId;
  }

  @Override
  public void setRange(TimeRange range) {
    if (getRange().getStartTime().equals(range.getStartTime())) return;
    if (range.getStartTime().isBefore(LocalDateTime.now().plusDays(1))) {
      NotificationManager.addNotifiersFor(this);
    }
    super.setRange(range);
  }

  @Nullable
  public Match getMatch() {
    final Integer matchId = getDetails().intValue();
    if (matchId == -1) return null;
    return Database.Find.find(Match.class, matchId);
  }

  @Override
  public List<String> getData() {
    final Match match = getMatch();
    return List.of(
        getRange().display(),
        match == null ? type.toString() : match.getTypeString(),
        match == null ? Util.avoidNull(getDetails(), "no data") : Util.avoidNull(match.getOpponentOf(orgaTeam.getTeam()), "kein Gegner", Team::getName)
    );
  }

  public String toString() {
    final Match match = getMatch();
    return match == null ? type.toString() : match.getTypeString();
  }

  @ExtensionMethod(StringUtils.class)
  public enum TeamCalendarType {
    KALIBRIERUNG, COACHING, CLASH, MEETING, TRAINING, MATCH;

    @Override
    public String toString() {
      return name().capitalizeFirst();
    }
  }
}
