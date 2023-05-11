package de.zahrie.trues.api.coverage.match.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

import de.zahrie.trues.api.coverage.match.log.LogFactory;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLogAction;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.discord.scouting.Scouting;
import de.zahrie.trues.discord.scouting.ScoutingManager;
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

    final Participator participator = LogFactory.handleUserWithTeam(match, userWithTeam);
    final var log = new MatchLog(timestamp, match, action, details, participator).create();
    final Team team = participator == null ? null : participator.getTeam();
    final String lastMessage = (team == null ? "ADMIN" : team.getAbbreviation()) + " : " + details;
    match.setLastMessage(lastMessage);

    if (participator != null && log.getAction().equals(MatchLogAction.LINEUP_SUBMIT)) {
      participator.getTeamLineup().setLineup(log, false);
    }
    match.getOrgaTeams().stream().map(ScoutingManager::forTeam).filter(Objects::nonNull).forEach(Scouting::sendLog);
    return true;
  }

  public LocalDateTime getLastLogTime() {
    return match.getLogs().stream().map(MatchLog::getTimestamp).max(Comparator.naturalOrder()).orElse(LocalDateTime.MIN);
  }

}
