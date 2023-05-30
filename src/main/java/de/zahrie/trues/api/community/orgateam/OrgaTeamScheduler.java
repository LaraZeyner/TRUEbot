package de.zahrie.trues.api.community.orgateam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.calendar.TeamCalendar;
import de.zahrie.trues.api.database.query.Query;

public record OrgaTeamScheduler(OrgaTeam team) {
  public List<TeamCalendar> getCalendarEntries() {
    final var limitTime = LocalDateTime.now().plusMinutes(30);
    final List<TeamCalendar> calendarEntries = new Query<>(TeamCalendar.class).where("orga_team", team).and("calendar_end", limitTime)
        .entityList();
    if (team.getTeam() == null) return List.of();
    team.getTeam().getMatches().getUpcomingMatches().stream()
        .map(match -> new TeamCalendar(match.getExpectedTimeRange(), String.valueOf(match.getId()), TeamCalendar.TeamCalendarType.MATCH, team, -1).create())
        .forEach(calendarEntries::add);

    return calendarEntries;
  }

  public List<TeamCalendar> getCalendarEntries(LocalDate localDate) {
    return getCalendarEntries().stream().filter(calendar -> calendar.getRange().getStartTime().toLocalDate().isEqual(localDate)).toList();
  }
}
