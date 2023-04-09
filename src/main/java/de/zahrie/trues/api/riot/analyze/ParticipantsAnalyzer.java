package de.zahrie.trues.api.riot.analyze;

import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.ParticipantStats;
import com.merakianalytics.orianna.types.core.match.Team;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.riot.matchhistory.performance.ParticipantExtension;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.PerformanceFactory;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

@Data
@ExtensionMethod(ParticipantExtension.class)
public class ParticipantsAnalyzer {
  private final Match match;
  private final TeamPerf teamPerformance;
  private final Team team;
  private final Participant participant;

  public Performance analyze() {
    final Player player = PlayerFactory.getPlayerFromPuuid(participant.getSummoner().getPuuid());
    final Performance performance = PerformanceFactory.getPerformanceByPlayerAndTeamPerformance(player, teamPerformance);
    if (performance != null) {
      return null;
    }
    final ParticipantStats stats = participant.getStats();
    final Lane lane = participant.getPlayedLane();
    final Champion selectedChampion = participant.getSelectedChampion();
    final Participant opponent = participant.getOpponent(match);
    final Champion opposingChampion = opponent == null ? null : opponent.getSelectedChampion();
    final KDA kda = participant.getKDA();
    return new Performance(teamPerformance, player, lane, selectedChampion, opposingChampion, kda, stats.getGoldEarned(), stats.getDamageDealt(), stats.getVisionScore(), stats.getCreepScore());
  }
}
