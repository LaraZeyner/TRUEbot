package de.zahrie.trues.api.coverage.match;

import java.util.List;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.match.model.Scrimmage;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.Util;
import org.jetbrains.annotations.Nullable;

public final class MatchFactory {

  public static List<Scrimmage> getUpcomingScrims() {
    return Database.Find.findList(Scrimmage.class, "upcoming");
  }
  public static Long getMatchOf(Team team, Team opponent) {
    return team.getParticipators().stream().filter(participator -> participator.getMessageId() != null)
        .filter(participator -> participator.getCoverage().getOpponent(team).getTeam().equals(opponent)).map(Participator::getMessageId)
        .findFirst().orElse(null);
  }

  public static List<Match> getMatchesOf(Team team, Team opponent) {
    return team.getParticipators().stream().map(Participator::getCoverage)
        .filter(coverage -> coverage.getParticipators().stream()
            .anyMatch(participator1 -> participator1.getTeam().equals(opponent)))
        .toList();
  }

  @Nullable
  public static PrimeMatch getMatch(int matchId) {
    PrimeMatch match = Database.Find.find(PrimeMatch.class, new String[]{"matchId"}, new Object[]{matchId}, "fromMatchId");
    if (match != null) {
      return match;
    }
    match = new MatchLoader(matchId).create().getMatch();
    Database.save(match);
    return match;
  }

  public static List<Match> getNextMatches(Team team) {
    final List<Participator> next = Database.Find.findList(Participator.class, new String[]{"team"}, new Object[]{team}, "nextForTeam");
    return next.stream().map(Participator::getCoverage).toList();
  }

  public static Match getNextMatch(Team team) {
    Participator next = Database.Find.find(Participator.class, new String[]{"team"}, new Object[]{team}, "nextForTeam");
    if (next == null) next = Database.Find.find(Participator.class, new String[]{"team"}, new Object[]{team}, "lastForTeam");
    return Util.avoidNull(next, null, Participator::getCoverage);
  }

  public static List<Team> getNextTeams() {
    return Database.Find.findList(Match.class, "nextMatches").stream().filter(Match::isOrgagame)
        .flatMap(nextOrgaMatch -> nextOrgaMatch.getParticipators().stream())
        .map(Participator::getTeam).distinct().toList();
  }
}
