package de.zahrie.trues.api.coverage.match.model;

import java.util.Calendar;
import java.util.List;

import de.zahrie.trues.api.coverage.match.log.LineupMatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.util.database.Database;
import de.zahrie.trues.util.util.Time;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrimeMatchImpl {
  private final PrimeMatch match;

  public boolean updateLogs(Calendar timestamp, String userWithTeam, MatchLogAction action, String details) {
    final Calendar calendar = getLastLogTime();
    if (timestamp.before(calendar)) {
      return false;
    }
    if (action.equals(MatchLogAction.lineup_player_ready)) {
      return false;
    }
    var log = new MatchLog(timestamp, action, match, details);
    log = log.handleTeam(userWithTeam);
    final Participator participator = log.getParticipator();
    final Team team = participator.getTeam();
    final String lastMessage = team.getAbbreviation() + " : " + details;
    match.setLastMessage(lastMessage);
    Database.save(log);

    if (log.getAction().equals(MatchLogAction.lineup_submit)) {
      final List<Player> newLineup = ((LineupMatchLog) log).determineLineup();
      participator.get().setLineup(newLineup);
    }

    return true;
  }

  public Calendar getLastLogTime() {
    return match.getLogs().stream().map(MatchLog::getTimestamp).max(Calendar::compareTo).orElse(Time.min());
  }

}
