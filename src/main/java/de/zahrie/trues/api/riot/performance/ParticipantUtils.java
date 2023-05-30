package de.zahrie.trues.api.riot.performance;

import java.util.List;

import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.match.Side;
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchTeam;

public class ParticipantUtils {
  public static List<MatchParticipant> getParticipants(LOLMatch match, TeamType side) {
    return match.getParticipants().stream().filter(matchParticipant -> matchParticipant.getTeam().equals(side)).toList();
  }

  public static Lane getPlayedLane(MatchParticipant participant) {
    return Lane.transform(participant.getLane());
  }

  public static Champion getSelectedChampion(MatchParticipant participant) {
    return new Query<>(Champion.class).entity(participant.getChampionId());
  }

  public static MatchParticipant getOpponent(MatchParticipant participant, LOLMatch match) {
    final Lane playedLane = getPlayedLane(participant);
    final Side side = Side.valueOf(participant.getTeam().name());

    final MatchTeam opposingTeam = side.getOpponent(match);
    if (opposingTeam == null) return null;

    final List<MatchParticipant> participants = ParticipantUtils.getParticipants(match, opposingTeam.getTeamId());
    return participants.stream().filter(part -> getPlayedLane(part).equals(playedLane)).findFirst().orElse(null);
  }

  public static Matchup getMatchup(MatchParticipant participant, LOLMatch match) {
    final MatchParticipant otherParticipant = getOpponent(participant, match);
    final Side side = Side.ofId(participant.getTeam().getValue());
    return new Matchup(
        getPlayedLane(participant),
        side.equals(Side.BLUE) ? participant : otherParticipant,
        side.equals(Side.RED) ? participant : otherParticipant
    );
  }

  public record Matchup(Lane lane, MatchParticipant blue, MatchParticipant red) {
  }
}
