package de.zahrie.trues.api.coverage.match.log;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.StringUtils;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Log
@ExtensionMethod(StringUtils.class)
public final class LogFactory {
  public static MatchLog handleUserWithTeam(MatchLog matchLog, String content) {
    content = content.between("(", ")", -1);
    if (content.equals("admin")) {
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
    Database.update(matchLog);
    return matchLog;
  }

}
