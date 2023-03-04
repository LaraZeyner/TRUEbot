package de.zahrie.trues.api.coverage.match.log;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.datatypes.symbol.Chain;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.logger.Logger;

/**
 * Created by Lara on 17.02.2023 for TRUEbot
 */
public final class LogFactory {

  public static MatchLog handleUserWithTeam(MatchLog log, Chain content) {
    content = content.between("(", ")", -1);
    if (content.toString().equals("admin")) {
      return log;
    }
    final Integer teamIndex = content.replace("Team ", "").intValue();
    if (teamIndex != null) {
      final Participator team;
      if (teamIndex == 1) {
        team = log.getMatch().getHome();
      } else if (teamIndex == 2) {
        team = log.getMatch().getGuest();
      } else {
        Logger.getLogger("LogFactory").warning(teamIndex + " spielt nicht - Log fehlerhaft");
        return log;
      }
      log.setParticipator(team);
      team.getLogs().add(log);
      Database.save(log);
    }
    return log;
  }

}
