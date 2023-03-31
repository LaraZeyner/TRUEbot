package de.zahrie.trues.api.coverage.match.model;

import java.util.List;

import de.zahrie.trues.api.coverage.match.log.LineupMatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.database.Database;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrimeMatchImpl {
  private final PrimeMatch match;

  public boolean updateLogs(Time timestamp, Chain userWithTeam, MatchLogAction action, String details) {
    final Time calendar = getLastLogTime();
    if (timestamp.before(calendar) || action.equals(MatchLogAction.LINEUP_PLAYER_READY)) {
      return false;
    }
    var log = new MatchLog(timestamp, action, match, details);
    log = log.handleTeam(userWithTeam);
    final Participator participator = log.getParticipator();
    final Team team = participator.getTeam();
    final String lastMessage = team.getAbbreviation() + " : " + details;
    match.setLastMessage(lastMessage);
    Database.save(log);

    if (log.getAction().equals(MatchLogAction.LINEUP_SUBMIT)) {
      final List<Player> newLineup = ((LineupMatchLog) log).determineLineup();
      participator.get().setLineup(newLineup);
    }

    return true;
  }

  public Time getLastLogTime() {
    return match.getLogs().stream().map(MatchLog::getTimestamp).max(Time::compareTo).orElse(Time.min());
  }

}
