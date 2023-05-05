package de.zahrie.trues.api.coverage.match.log;

import de.zahrie.trues.api.coverage.match.model.AMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.util.StringUtils;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Log
@ExtensionMethod(StringUtils.class)
public final class LogFactory {
  public static Participator handleUserWithTeam(AMatch AMatch, String content) {
    content = content.between("(", ")", -1);
    if (content.equals("admin")) return null;

    final int teamIndex = content.replace("Team ", "").intValue();
    return switch (teamIndex) {
      case 1 -> AMatch.getHome();
      case 2 -> AMatch.getGuest();
      default -> throw new IllegalArgumentException("Matchlog fehlerhaft");
    };
  }

}
