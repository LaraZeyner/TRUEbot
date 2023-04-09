package de.zahrie.trues.api.coverage.lineup;

import java.util.List;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.Rank;
import lombok.Getter;

@Getter
public class MatchLineup {
  private final Participator participator;
  private final List<Lineup> lineup;

  public MatchLineup(Participator participator) {
    this.participator = participator;
    this.lineup = LineupFinder.getLineup(participator);
  }

  public String getAverageElo() {
    return Rank.fromMMR(getAverageMMR()).toString();
  }

  public int getAverageMMR() {
    final double averageMMR = lineup.stream().map(Lineup::getPlayer)
        .map(Player::getLastRelevantRank)
        .mapToInt(Rank::getMMR).average().orElse(0);
    return (int) Math.round(averageMMR);
  }
}
