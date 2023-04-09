package de.zahrie.trues.api.coverage.match;

import java.util.List;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.match.model.Scrimmage;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public final class MatchFactory {

  public static List<Scrimmage> getUpcomingScrims() {
    return QueryBuilder.hql(Scrimmage.class, "FROM Scrimmage WHERE result = '-:-' ORDER BY start").list();
  }

  public static List<Match> getMatchesOf(Team team, Team opponent) {
    return team.getParticipators().stream().map(Participator::getCoverage)
        .filter(coverage -> coverage.getParticipators().stream()
            .anyMatch(participator1 -> participator1.getTeam().equals(opponent)))
        .toList();
  }

  @Nullable
  public static PRMMatch getMatch(int matchId) {
    PRMMatch match = QueryBuilder.hql(PRMMatch.class, "FROM PRMMatch WHERE matchId = " + matchId).single();
    if (match != null) {
      return match;
    }
    match = new MatchLoader(matchId).create().getMatch();
    Database.save(match);
    return match;
  }

  public static List<Match> getNextMatches(Team team) {
    return QueryBuilder.hql(Match.class,
        "SELECT coverage FROM Participator WHERE team = " + team + " AND coverage.result = '-:-' ORDER BY coverage").list();
  }

  @Nullable
  public static Match getNextMatch(Team team) {
    Participator next = QueryBuilder.hql(Participator.class,
        "FROM Participator WHERE team = " + team + " AND coverage.result = '-:-' ORDER BY coverage").single();
    if (next == null) next = QueryBuilder.hql(Participator.class,
        "FROM Participator WHERE team = " + team + " AND coverage.result <> '-:-' ORDER BY coverage desc").single();
    return Util.avoidNull(next, null, Participator::getCoverage);
  }

  public static List<Team> getNextTeams() {
    return QueryBuilder.hql(Match.class, "FROM Match WHERE start > NOW() OR result = '-:-' ORDER BY start").list()
        .stream().filter(Match::isOrgagame)
        .flatMap(nextOrgaMatch -> nextOrgaMatch.getParticipators().stream())
        .map(Participator::getTeam).distinct().toList();
  }
}
