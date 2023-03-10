package de.zahrie.trues.api.coverage.participator;

import java.util.List;

import de.zahrie.trues.api.coverage.lineup.LineupFactory;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.Lane;
import de.zahrie.trues.database.Database;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParticipatorImpl {
  private final Participator participator;

  public void setLineup(List<Player> newLineup) {
    setLineup(newLineup, false);
  }

  public void setOrderedLineup(List<Player> newLineup) {
    setLineup(newLineup, true);
  }

  private void setLineup(List<Player> newLineup, boolean ordered) {
    for (final Lineup lineup : participator.getLineups()) {
      if (!newLineup.contains(lineup.getPlayer())) {
        participator.getLineups().remove(lineup);
        lineup.setParticipator(null);
        Database.remove(lineup);
      }
    }

    for (Player player : newLineup) {
      final Lane lane = determineLane(newLineup, ordered, player);
      if (LineupFactory.determineLineup(participator, player) == null) {
        addLineup(new Lineup(participator, lane, player));
      }
    }
    Database.save(this);
  }

  private static Lane determineLane(List<Player> newLineup, boolean ordered, Player player) {
    if (ordered) {
      final int index = newLineup.indexOf(player);
      return Lane.values()[index + 1];
    }
    return Lane.UNKNOWN;
  }

  private void addLineup(Lineup lineup) {
    participator.getLineups().add(lineup);
    lineup.setParticipator(participator);
    Database.save(lineup);
  }

}
