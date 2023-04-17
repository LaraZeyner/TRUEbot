package de.zahrie.trues.api.calendar.scheduling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.calendar.CalendarBase;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.datatypes.calendar.DateTimeUtils;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.util.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({StringUtils.class, DateTimeUtils.class})
@RequiredArgsConstructor
@Data
public class SchedulingHandler {
  private final DiscordUser user;

  public static boolean isRepeat(String input) {
    return Arrays.stream(input.replace("\n", " ").split(" ")).allMatch(section -> section.equals("repeat") || section.contains("@"));
  }

  public void repeat() {
    final LocalDate start = LocalDate.now().minusWeeks(1);
    final LocalDate end = LocalDate.now().minusDays(1);

    QueryBuilder.hql(SchedulingCalendar.class,
            "FROM SchedulingCalendar WHERE user = " + user.getId() + " AND details <> 'urlaub' AND date(startTime) between" + start + " and " + end)
        .list().stream()
        .map(schedulingCalendar -> new SchedulingCalendar(schedulingCalendar.getRange().plusWeeks(1), "", user)).forEach(Database::insert);
  }

  public static List<TimeRange> determineTimeRanges(String input) {
    return new DateTimeStringConverter(input).toRangeList();
  }

  public void add(List<TimeRange> ranges) {
    if (ranges == null || ranges.isEmpty()) return;
    delete(ranges);
    final List<TimeRange> combine = TimeRange.combine(ranges);
    combine.stream().map(betterTimeRange -> new SchedulingCalendar(betterTimeRange, "", user)).forEach(Database::insert);
  }

  public void delete(List<TimeRange> ranges) {
    ranges.stream().map(TimeRange::getStartTime).map(LocalDateTime::toLocalDate).distinct().forEach(localDate -> QueryBuilder.hql(SchedulingCalendar.class,
            "FROM SchedulingCalendar WHERE user = " + user.getId() + " AND details <> 'urlaub' AND date(range.startTime) = " + localDate)
        .list().forEach(Database::remove));
  }

  public List<TimeRange> getRemaining(LocalDate from) {
    final List<SchedulingCalendar> all = QueryBuilder.hql(SchedulingCalendar.class,
        "FROM SchedulingCalendar WHERE user = " + user.getId() + " AND date(range.startTime) >= " + from).list();
    final List<SchedulingCalendar> reduceable = QueryBuilder.hql(SchedulingCalendar.class,
        "FROM SchedulingCalendar WHERE user = " + user.getId() + " AND details = 'urlaub'").list();
    return TimeRange.reduce(new ArrayList<>(all.stream().map(SchedulingCalendar::getRange).toList()),
        new ArrayList<>(reduceable.stream().map(SchedulingCalendar::getRange).toList()));
  }

  public List<TimeRange> getRemainingAt(LocalDate at) {
    return getRemaining(at).stream().filter(timeRange -> timeRange.getStartTime().toLocalDate().equals(at)).toList();
  }

  public List<TimeRange> getFreeAt(LocalDate at) {
    final List<TimeRange> availableRanges = new ArrayList<>(getRemainingAt(at));
    final List<TimeRange> blockedRanges = new ArrayList<>();
    user.getMainMemberships().stream().map(Membership::getOrgaTeam)
        .filter(Objects::nonNull).findFirst()
        .ifPresent(orgaTeam ->
            blockedRanges.addAll(orgaTeam.getScheduler().getCalendarEntries(at).stream().map(CalendarBase::getRange).toList()));
    return TimeRange.reduce(availableRanges, blockedRanges);
  }

  public List<TimeRange> getRemainingFromTo(LocalDate from, LocalDate to) {
    return getRemaining(from).stream().filter(timeRange -> timeRange.getStartTime().isBetween(from, to)).toList();
  }
}
