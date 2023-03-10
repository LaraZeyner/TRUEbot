package de.zahrie.trues.api.coverage.lineup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.api.riot.Lane;
import lombok.Getter;

/**
 * Created by Lara on 24.02.2023 for TRUEbot
 */
@Getter
public class LineupCreator extends LineupCreatorBase {
  private final Participator participator;
  private final Set<Player> players;
  private final Set<Player> blacklist = new LinkedHashSet<>();

  public LineupCreator(Participator participator) {
    super();
    this.participator = participator;
    this.players = determinePlayers();

    for (final Player player : players) {
      handleLineup(player, participator);
    }
    sort();
  }

  private Set<Player> determinePlayers() {
    final Set<Player> players = participator.getLineups().stream().map(Lineup::getPlayer).collect(Collectors.toSet());
    if (participator.getLineups().size() == 5) {
      return players;
    }
    players.addAll(participator.getTeam().getPlayers());
    return players;
  }

  private void handleLineup(Player player, Participator participator) {
    final Time time = new Time(-180);
    final Team team = participator.getTeam();
    final List<Object[]> list = Database.Find.findObjectList(new String[]{"player", "team", "start"}, new Object[]{player, team, time},
        "Player.getLanePlayed");
    for (Object[] o : list) {
      final Lane lane = Lane.valueOf(o[0].toString().toUpperCase());
      final Lineup atPosition = LineupFactory.determineLineup(participator, lane);
      if (atPosition == null || (atPosition.getPlayer().equals(player) && !blacklist.contains(atPosition.getPlayer()))) {
        final int amount = (int) o[1];
        add(lane, player, amount);
      }
      if (atPosition != null) {
        blacklist.add(atPosition.getPlayer());
      }
    }
  }

  public List<Lineup> handleLineup() {
    final List<Player> determineLineup = determineLineup();
    return Arrays.stream(Lane.values()).filter(lane -> lane.ordinal() > 0)
        .map(lane -> determineLineup(lane, determineLineup.get(lane.ordinal() - 1)))
        .toList();
  }

  private Lineup determineLineup(Lane lane, Player player) {
    Lineup lineup = LineupFactory.determineLineup(participator, lane);
    if (lineup != null) {
      return lineup;
    }

    lineup = LineupFactory.determineLineup(participator, player);
    if (lineup != null) {
      lineup.setLane(lane);
      return lineup;
    }

    return new Lineup(participator, lane, player);
  }

  private List<Player> determineLineup() {
    int totalAmount = Integer.MIN_VALUE;
    List<Player> bestLineup = new ArrayList<>();
    for (final LaneGames topLineup : laneLineups.get(Lane.TOP).getPlayers()) {
      final Player topPlayer = topLineup.player();
      final int topAmount = topLineup.amount();

      for (final LaneGames jglLineup : laneLineups.get(Lane.JUNGLE).getPlayers()) {
        final Player jglPlayer = jglLineup.player();
        final int jglAmount = jglLineup.amount();

        for (final LaneGames midLineup : laneLineups.get(Lane.MIDDLE).getPlayers()) {
          final Player midPlayer = midLineup.player();
          final int midAmount = midLineup.amount();

          for (final LaneGames botLineup : laneLineups.get(Lane.BOTTOM).getPlayers()) {
            final Player botPlayer = botLineup.player();
            final int botAmount = botLineup.amount();

            for (final LaneGames supLineup : laneLineups.get(Lane.UTILITY).getPlayers()) {
              final Player supPlayer = supLineup.player();
              final int supAmount = supLineup.amount();

              final int amount = topAmount + jglAmount + midAmount + botAmount + supAmount;
              if (amount > totalAmount) {
                totalAmount = amount;
                bestLineup = List.of(topPlayer, jglPlayer, midPlayer, botPlayer, supPlayer);
              }
            }
          }
        }
      }
    }
    return bestLineup;
  }

}
