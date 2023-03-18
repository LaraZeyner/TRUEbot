package de.zahrie.trues.api.coverage.match.log;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.database.Database;
import lombok.extern.java.Log;

@Log
public final class LogFactory {
  public static MatchLog handleUserWithTeam(MatchLog matchLog, Chain content) {
    content = content.between("(", ")", -1);
    if (content.toString().equals("admin")) {
      return matchLog;
    }
    final int teamIndex = content.replace("Team ", "").intValue();
    final Participator team;
    switch (teamIndex) {
      case 1 -> team = matchLog.getMatch().getHome();
      case 2 -> team = matchLog.getMatch().getGuest();
      default -> {
        log.warning(teamIndex + " spielt nicht - Log fehlerhaft");
        return matchLog;
      }
    }
    matchLog.setParticipator(team);
    team.getLogs().add(matchLog);
    Database.save(matchLog);
    return matchLog;
  }

}
