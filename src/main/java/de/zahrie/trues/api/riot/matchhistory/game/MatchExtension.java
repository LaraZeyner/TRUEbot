package de.zahrie.trues.api.riot.matchhistory.game;

import com.merakianalytics.orianna.types.core.match.Match;
import de.zahrie.trues.api.datatypes.calendar.Time;

public class MatchExtension {
  public static GameType getGameQueue(Match match) {
    if (match.getCoreData().getQueue() == 0) return match.getTournamentCode().isEmpty() ? GameType.CUSTOM : GameType.TOURNAMENT;
    else return GameType.fromId(match.getCoreData().getQueue());
  }

  public static String getMatchId(Match match) {
    return match.getPlatform().getTag() + "_" + match.getId();
  }

  public static Time getCreation(Match match) {
    return Time.of(match.getCreationTime().toDate());
  }
}
