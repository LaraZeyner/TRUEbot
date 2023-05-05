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
import de.zahrie.trues.api.riot.matchhistory.performance.Matchup;
import de.zahrie.trues.api.riot.matchhistory.performance.ParticipantUtils;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.TeamPerf;

public record ParticipantsAnalyzer(Match match, TeamPerf teamPerformance, Team team, Participant participant) {
  public Performance analyze() {
    final Player player = PlayerFactory.getPlayerFromPuuid(participant.getSummoner().getPuuid());
    final ParticipantStats stats = participant.getStats();
    final Lane lane = ParticipantUtils.getPlayedLane(participant);
    final Champion selectedChampion = ParticipantUtils.getSelectedChampion(participant);
    final Participant opponent = ParticipantUtils.getOpponent(participant, match);
    final Champion opposingChampion = opponent == null ? null : ParticipantUtils.getSelectedChampion(opponent);
    final Matchup matchup = new Matchup(selectedChampion, opposingChampion);
    final KDA kda = KDA.fromParticipant(participant);
    return new Performance(teamPerformance, player, lane, matchup, kda, stats.getGoldEarned(), stats.getDamageDealt(), stats.getVisionScore(), stats.getCreepScore()).create();
  }
}
