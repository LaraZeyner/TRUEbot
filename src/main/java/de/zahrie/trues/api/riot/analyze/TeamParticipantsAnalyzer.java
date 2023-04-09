package de.zahrie.trues.api.riot.analyze;

import java.util.List;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.Team;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.game.MatchExtension;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@Data
@RequiredArgsConstructor
@ExtensionMethod(MatchExtension.class)
public class TeamParticipantsAnalyzer {
  private final Match match;
  private final Team team;

  public List<Participant> analyze() {
    if (match.getGameQueue().equals(GameType.TOURNAMENT) || match.getGameQueue().equals(GameType.CUSTOM)) {
      return team.getParticipants();
    }

    final List<Participant> players = team.getParticipants().stream()
        .filter(matchParticipant -> PlayerFactory.findPlayer(matchParticipant.getSummoner().getPuuid()) != null).toList();
    return match.getGameQueue().equals(GameType.CLASH) && !players.isEmpty() ? team.getParticipants() : players;
  }
}
