package de.zahrie.trues.api.coverage.lineup;

import java.util.List;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import lombok.Getter;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
@Getter
public class MatchLineup {
  private final Participator participator;
  private final List<Lineup> lineup;

  public MatchLineup(Participator participator) {
    this.participator = participator;
    this.lineup = handleLineup(participator);
  }


  private List<Lineup> handleLineup(Participator participator) {
    final var lineupCreator = new LineupCreator(participator);
    return lineupCreator.handleLineup();
  }

}
