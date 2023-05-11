package de.zahrie.trues.api.coverage.match.log;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.io.log.DevInfo;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
@ExtensionMethod(StringUtils.class)
public final class LogFactory {
  @Nullable
  public static Participator handleUserWithTeam(Match match, String content) {
    content = content.between("(", ")", -1);
    if (content.equals("admin")) return null;

    final int teamIndex = content.replace("Team ", "").intValue();
    return switch (teamIndex) {
      case 1 -> match.getHome();
      case 2 -> match.getGuest();
      default -> {
        final PRMTeam prmTeam = new Query<>(PRMTeam.class).where("prm_id", teamIndex).entity();
        if (prmTeam == null) {
          final RuntimeException exception = new IllegalArgumentException("Matchlog fehlerhaft");
          new DevInfo(teamIndex + "").severe(exception);
          throw exception;
        }
        yield match.getParticipator(prmTeam);
      }
    };
  }
}
