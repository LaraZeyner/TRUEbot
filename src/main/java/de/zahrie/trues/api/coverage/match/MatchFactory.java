package de.zahrie.trues.api.coverage.match;

import java.util.List;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.database.Database;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public final class MatchFactory {

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

  public static List<Team> getNextTeams() {
    return Database.Find.findList(Match.class, "nextOrgaMatches").stream().filter(Match::isOrgagame)
        .flatMap(nextOrgaMatch -> nextOrgaMatch.getParticipators().stream())
        .map(Participator::getTeam).distinct().toList();
  }
}
