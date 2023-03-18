package de.zahrie.trues.api.coverage.lineup;

import java.util.List;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.match.model.Match;
import lombok.Getter;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
@Getter
public class MatchLineups {
  private final Match match;
  private List<MatchLineup> lineups;

  public MatchLineups(Match match) {
    this.match = match;
    this.lineups = match.getParticipators().stream().map(MatchLineup::new).toList();
  }

  public MatchLineup getLineup(Participator participator) {
    // TODO (Abgie) 01.03.2023: never used
    return lineups.stream().filter(lineup -> lineup.getParticipator().equals(participator)).findFirst().orElse(null);
  }

  public void update() {
    lineups = match.getParticipators().stream().map(MatchLineup::new).toList();
  }

}
