package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.DTO;
import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.discord.notify.NotificationManager;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Table(value = "calendar", department = "team")
@ExtensionMethod(StringUtils.class)
public class TeamCalendar extends EventCalendarBase implements Entity<TeamCalendar>, DTO<Calendar> {
  @Serial
  private static final long serialVersionUID = -8449986995823183145L;

  private TeamCalendarType type; // calendar_type
  private OrgaTeam orgaTeam; // orga_team

  public TeamCalendar(TimeRange timeRange, String details, TeamCalendarType type, OrgaTeam orgaTeam, long threadId) {
    super(timeRange, details, threadId);
    this.type = type;
    this.orgaTeam = orgaTeam;
    this.threadId = threadId;
  }

  public TeamCalendar(int id, TimeRange range, String details, Long threadId, TeamCalendarType type, OrgaTeam orgaTeam) {
    super(id, range, details, threadId);
    this.type = type;
    this.orgaTeam = orgaTeam;
  }

  public static TeamCalendar get(List<Object> objects) {
    return new TeamCalendar(
        (int) objects.get(0),
        new TimeRange((LocalDateTime) objects.get(2), (LocalDateTime) objects.get(3)),
        (String) objects.get(4),
        (Long) objects.get(6),
        new SQLEnum<>(TeamCalendarType.class).of(objects.get(5)),
        new Query<>(OrgaTeam.class).entity(objects.get(8))
    );
  }

  @Override
  public TeamCalendar create() {
    final var calendar = new Query<>(TeamCalendar.class)
        .col("calendar_start", range.getStartTime()).col("calendar_end", range.getEndTime()).col("details", details)
        .col("thread_id", threadId).col("calendar_type", type).col("orga_team", orgaTeam)
        .insert(this);
    if (range.getStartTime().isBefore(LocalDateTime.now().plusDays(1))) {
      NotificationManager.addNotifiersFor(calendar);
    }
    return calendar;
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
    final String details = getDetails();
    if (details == null) return null;

    final Integer matchId = getDetails().intValue();
    if (matchId == -1) return null;

    return new Query<>(Match.class).entity(matchId);
  }

  @Override
  public List<Object> getData() {
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
  @Listing(Listing.ListingType.UPPER)
  public enum TeamCalendarType {
    KALIBRIERUNG, COACHING, CLASH, MEETING, TRAINING, MATCH;

    @Override
    public String toString() {
      return name().capitalizeFirst();
    }
  }
}
