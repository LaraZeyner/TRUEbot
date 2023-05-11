package de.zahrie.trues.api.riot.game;

import java.time.LocalDateTime;

import com.merakianalytics.orianna.types.core.match.Match;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class MatchUtils {
  public static GameType getGameQueue(Match match) {
    if (match.getCoreData().getQueue() == 0) return match.getTournamentCode().isEmpty() ? GameType.CUSTOM : GameType.TOURNAMENT;
    else return GameType.fromId(match.getCoreData().getQueue());
  }

  public static String getMatchId(Match match) {
    return match.getPlatform().getTag() + "_" + match.getId();
  }

  public static LocalDateTime getCreation(Match match) {
    final DateTime dateTime = match.getCreationTime().withZone(DateTimeZone.getDefault());
    return LocalDateTime.of(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), dateTime.getSecondOfMinute());
  }
}
