package de.zahrie.trues.api.riot.analyze;

import java.util.List;

import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.xayah.types.core.match.Match;
import de.zahrie.trues.api.riot.xayah.types.core.match.MatchParticipant;
import de.zahrie.trues.api.riot.xayah.types.core.match.Team;

public record TeamParticipantsAnalyzer(Match match, Team team) {
  public List<MatchParticipant> analyze() {
    if (match.getGameQueue().equals(GameType.TOURNAMENT) || match.getGameQueue().equals(GameType.CUSTOM)) {
      return team.getParticipants();
    }

    final List<MatchParticipant> players = team.getParticipants().stream()
        .filter(matchParticipant -> PlayerFactory.findPlayer(matchParticipant.getSummoner().getPuuid()) != null).toList();
    return match.getGameQueue().equals(GameType.CLASH) && !players.isEmpty() ? team.getParticipants() : players;
  }
}
