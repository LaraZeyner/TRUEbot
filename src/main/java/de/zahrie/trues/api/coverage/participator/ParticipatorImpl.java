package de.zahrie.trues.api.coverage.participator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.coverage.lineup.LineupFactory;
import de.zahrie.trues.api.coverage.lineup.LineupManager;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.database.Database;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ParticipatorImpl {
  private final Participator participator;

  public void setLineup(List<Player> newLineup) {
    setLineup(newLineup, false);
  }

  public void setOrderedLineup(List<Player> newLineup) {
    setLineup(newLineup, true);
  }

  public boolean setOrderedLineup(@NotNull String opGgUrl) {
    return setOrderedLineup(opGgUrl, new ArrayList<>(5));
  }

  public boolean setOrderedLineup(@NotNull String opGgUrl, @NotNull List<Player> players) {
    final String[] split = opGgUrl.replace("https://www.op.gg/multisearch/euw?summoners=", "").split("%2C");
    for (int i = 0; i < split.length; i++) {
      if (i > 4) break;

      final String summonerName = split[i];
      if (summonerName.isBlank()) continue;

      final Player player = PlayerFactory.getPlayerFromName(summonerName);
      if (player != null) players.set(i, player);
    }
    if (players.stream().anyMatch(Objects::isNull)) return false;
    setOrderedLineup(players);
    return true;
  }

  private void setLineup(List<Player> newLineup, boolean ordered) {
    participator.getLineups().stream().filter(lineup -> !newLineup.contains(lineup.getPlayer())).forEach(participator::removeLineup);

    for (Player player : newLineup) {
      final Lane lane = determineLane(newLineup, ordered, player);
      if (LineupFactory.determineLineup(participator, player) == null) {
        final var lineup = new Lineup(lane, player);
        Database.insert(lineup);
        participator.addLineup(lineup);
      }
    }
    Database.update(this);
    LineupManager.getMatch(participator.getCoverage()).update();
  }

  private static Lane determineLane(List<Player> newLineup, boolean ordered, Player player) {
    if (!ordered) return Lane.UNKNOWN;
    final int index = newLineup.indexOf(player);
    return Lane.values()[index + 1];
  }

}
