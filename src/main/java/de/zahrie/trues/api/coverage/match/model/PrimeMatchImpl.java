package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import de.zahrie.trues.api.coverage.match.log.LineupMatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrimeMatchImpl {
  private final PRMMatch match;

  public String getURL() {
    return "https://www.primeleague.gg/leagues/matches/" + match.getMatchId();
  }

  public boolean updateLogs(LocalDateTime timestamp, String userWithTeam, MatchLogAction action, String details) {
    final LocalDateTime lastLogTime = getLastLogTime();
    if (timestamp.isBefore(lastLogTime) || action.equals(MatchLogAction.LINEUP_PLAYER_READY)) return false;

    var log = new MatchLog(timestamp, action, details);
    match.addLog(log);
    log = log.handleTeam(userWithTeam);
    final Participator participator = log.getParticipator();
    final Team team = participator.getTeam();
    final String lastMessage = team.getAbbreviation() + " : " + details;
    match.setLastMessage(lastMessage);
    Database.insert(log);

    if (log.getAction().equals(MatchLogAction.LINEUP_SUBMIT)) {
      final List<Player> newLineup = ((LineupMatchLog) log).determineLineup();
      participator.get().setLineup(newLineup);
    }

    return true;
  }

  public LocalDateTime getLastLogTime() {
    return match.getLogs().stream().map(MatchLog::getTimestamp).max(Comparator.naturalOrder()).orElse(LocalDateTime.MIN);
  }

}
