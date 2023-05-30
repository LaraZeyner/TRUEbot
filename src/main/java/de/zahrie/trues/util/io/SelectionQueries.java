package de.zahrie.trues.util.io;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SelectionQueries {
  public static final String ORGA_TEAMS = "SELECT team_name_created FROM orga_team";
  public static final String UPCOMING_MATCHES = "SELECT (SELECT team_name FROM coverage_team JOIN team t on coverage_team.team = t.team_id WHERE first = true and coverage = coverage_id), (SELECT team_name FROM coverage_team JOIN team t on coverage_team.team = t.team_id WHERE first = false and coverage = coverage_id) FROM coverage WHERE coverage_start >= NOW() ORDER BY coverage_start";
  public static final String PENDING_APPLICATIONS = "SELECT CONCAT(application_id, '. - ', mention, ' (', lineup_role, ' - ', position, ')') FROM application JOIN discord_user on application.discord_user = discord_user_id WHERE waiting = true order by app_timestamp";

  public static final String ORGA_GAMES = "";
  public static final String ORGA_CHAMPIONS = "";

}
