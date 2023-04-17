package de.zahrie.trues.api.community.orgateam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.calendar.TeamCalendar;
import de.zahrie.trues.api.database.QueryBuilder;

public record OrgaTeamScheduler(OrgaTeam team) {
  public List<TeamCalendar> getCalendarEntries() {
    final var limitTime = LocalDateTime.now().plusMinutes(30);
    final List<TeamCalendar> calendarEntries = QueryBuilder.hql(TeamCalendar.class, "FROM TeamCalendar WHERE orgaTeam = :team and range.endTime >= :end ORDER BY range.startTime").addParameters(Map.of("team", team, "end", limitTime)).list();
    team.getTeam().getMatches().getUpcomingMatches().stream().map(match -> TeamCalendar.create(match.getExpectedTimeRange(), String.valueOf(match.getId()), TeamCalendar.TeamCalendarType.MATCH, team, -1)).forEach(calendarEntries::add);

    return calendarEntries;
  }

  public List<TeamCalendar> getCalendarEntries(LocalDate localDate) {
    return getCalendarEntries().stream().filter(calendar -> calendar.getRange().getStartTime().toLocalDate().isEqual(localDate)).toList();
  }
}
