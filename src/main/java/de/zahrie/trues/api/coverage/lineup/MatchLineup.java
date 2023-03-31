package de.zahrie.trues.api.coverage.lineup;

import java.util.List;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import lombok.Getter;

@Getter
public class MatchLineup {
  private final Participator participator;
  private final List<Lineup> lineup;

  public MatchLineup(Participator participator) {
    this.participator = participator;
    this.lineup = new LineupCreator(participator).handleLineup();
  }
}
