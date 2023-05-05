package de.zahrie.trues.api.coverage.lineup;

import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import lombok.Getter;

@Getter
public class MatchLineups {
  private final Match match;
  private List<MatchLineup> lineups;

  public MatchLineups(Match match) {
    this.match = match;
    update();
  }

  public MatchLineup getLineup(Participator participator) {
    return lineups.stream().filter(lineup -> lineup.getParticipator().equals(participator)).findFirst().orElse(null);
  }

  public void update() {
    this.lineups = Arrays.stream(match.getParticipators()).map(MatchLineup::new).toList();
  }

}
