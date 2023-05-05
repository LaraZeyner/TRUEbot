package de.zahrie.trues.api.riot.analyze;

import java.util.List;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.Team;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.game.MatchUtils;

public record TeamParticipantsAnalyzer(Match match, Team team) {
  public List<Participant> analyze() {
    if (MatchUtils.getGameQueue(match).equals(GameType.TOURNAMENT) || MatchUtils.getGameQueue(match).equals(GameType.CUSTOM)) {
      return team.getParticipants();
    }

    final List<Participant> players = team.getParticipants().stream()
        .filter(matchParticipant -> PlayerFactory.findPlayer(matchParticipant.getSummoner().getPuuid()) != null).toList();
    return MatchUtils.getGameQueue(match).equals(GameType.CLASH) && !players.isEmpty() ? team.getParticipants() : players;
  }
}
