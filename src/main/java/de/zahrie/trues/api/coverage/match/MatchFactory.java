package de.zahrie.trues.api.coverage.match;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.AMatch;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.match.model.Scrimmage;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import org.jetbrains.annotations.Nullable;

public final class MatchFactory {
  public static List<Scrimmage> getUpcomingScrims() {
    return new Query<Scrimmage>().where("result", "-:-").ascending("coverage_start").entityList();
  }

  public static List<Match> getMatchesOf(TeamBase team, TeamBase opponent) {
    return new Query<Participator>().get("coverage").where("team", team)
        .with(new Query<Participator>().get("coverage").where("team", opponent))
        .convertList(Match.class);
  }

  @Nullable
  public static PRMMatch getMatch(int matchId) {
    final PRMMatch match = new Query<PRMMatch>().where("match_id", matchId).entity();
    if (match != null) return match;

    return new MatchLoader(matchId).create().getMatch();
  }

  public static List<TeamBase> getNextTeams() {
    return new Query<Participator>().join(new JoinQuery<Participator, Match>("coverage","_match"))
        .where(Condition.Comparer.GREATER_EQUAL, "_match.coverage_start", LocalDateTime.now()).or("result", "-:-")
        .or("result", "-:-")
        .ascending("_match.coverage_start").convertList(Match.class)
        .stream().filter(AMatch::isOrgagame)
        .flatMap(nextOrgaMatch -> Arrays.stream(nextOrgaMatch.getParticipators()))
        .map(Participator::getTeam).distinct().toList();
  }
}
