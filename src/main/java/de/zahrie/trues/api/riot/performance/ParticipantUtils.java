package de.zahrie.trues.api.riot.performance;

import com.merakianalytics.orianna.types.common.Side;
import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.Team;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.champion.ChampionFactory;

public class ParticipantUtils {
  public static Lane getPlayedLane(Participant participant) {
    return Lane.transform(participant.getLane());
  }

  public static Champion getSelectedChampion(Participant participant) {
    return ChampionFactory.getChampion(participant.getChampion());
  }

  public static Participant getOpponent(Participant participant, Match match) {
    final Lane playedLane = getPlayedLane(participant);
    final Side side = participant.getTeam().getSide();
    final Team opposingTeam = side.equals(Side.BLUE) ? match.getBlueTeam() : match.getRedTeam();
    return opposingTeam.getParticipants().stream().filter(part -> getPlayedLane(part).equals(playedLane)).findFirst().orElse(null);
  }

  public static Matchup getMatchup(Participant participant, Match match) {
    final Participant otherParticipant = getOpponent(participant, match);
    final Side side = participant.getTeam().getSide();
    return new Matchup(
        getPlayedLane(participant),
        side.equals(Side.BLUE) ? participant : otherParticipant,
        side.equals(Side.RED) ? participant : otherParticipant
    );
  }

  public record Matchup(Lane lane, Participant blue, Participant red) {
  }
}
