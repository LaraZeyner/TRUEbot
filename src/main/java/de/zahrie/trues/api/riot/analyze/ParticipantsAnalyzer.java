package de.zahrie.trues.api.riot.analyze;

import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.PerformanceFactory;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.core.match.Match;
import de.zahrie.trues.api.riot.xayah.types.core.match.MatchParticipant;
import de.zahrie.trues.api.riot.xayah.types.core.match.ParticipantStats;
import de.zahrie.trues.api.riot.xayah.types.core.match.Team;

public record ParticipantsAnalyzer(Match match, TeamPerf teamPerformance, Team team, MatchParticipant participant) {
  public Performance analyze() {
    final Player player = PlayerFactory.getPlayerFromPuuid(participant.getSummoner().getPuuid());
    final Performance performance = PerformanceFactory.getPerformanceByPlayerAndTeamPerformance(player, teamPerformance);
    if (performance == null) {
      final ParticipantStats stats = participant.getStats();
      final Lane lane = participant.getLane();
      final MatchParticipant opponent = getOpposingParticipant(lane);
      final Champion selectedChampion = participant.getChampion();
      final Champion opposingChampion = opponent == null ? null : opponent.getChampion();
      final Performance.KDA kda = new Performance.KDA((short) stats.getKills(), (short) stats.getDeaths(), (short) stats.getAssists());
      return new Performance(teamPerformance, player, lane, selectedChampion, opposingChampion, kda, stats.getGoldEarned(), stats.getDamageDealt(), stats.getVisionScore(), stats.getCreepScore());
    }
    return null;
  }

  private MatchParticipant getOpposingParticipant(Lane lane) {
    final Team opposingTeam = participant.getTeam().getSide().equals(Side.BLUE) ? match.getBlueTeam() : match.getRedTeam();
    return opposingTeam.getParticipants().stream().filter(participant -> participant.getLane().equals(lane)).findFirst().orElse(null);
  }
}
