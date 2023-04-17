package de.zahrie.trues.api.coverage.lineup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.io.cfg.LogFiles;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Getter
@ExtensionMethod(StringUtils.class)
@Log
public class LineupCreator extends LineupCreatorBase {
  private final Participator participator;
  private final Set<Player> players;
  private final Set<Player> blacklist = new LinkedHashSet<>();

  public LineupCreator(Participator participator) {
    this(participator, ScoutingGameType.TEAM_GAMES);
  }

  public LineupCreator(Participator participator, ScoutingGameType gameType) {
    this(participator, gameType, 180);
  }

  public LineupCreator(Participator participator, ScoutingGameType gameType, int days) {
    super();
    this.participator = participator;
    this.players = determinePlayers();
    players.forEach(player -> handleLineup(player, gameType, days));
    handleSelectedLineup();
    handleNoPlayers();
    sort();
  }

  public List<LaneGames> getPlayersOnLane(Lane lane) {
    final LaneLineup laneLineup = laneLineups.get(lane);
    return laneLineup == null ? List.of() : laneLineup.getPlayers();
  }

  private Set<Player> determinePlayers() {
    final Set<Player> players = participator.getLineups().stream().map(Lineup::getPlayer).collect(Collectors.toSet());
    if (participator.getLineups().size() == 5) return players;

    players.addAll(participator.getTeam().getPlayers());
    return players;
  }

  private void handleLineup(Player player, ScoutingGameType gameType, int days) {
    final List<Object[]> list = gameType.playerQuery(player, days).performance("lane, count(p)", "GROUP BY lane ORDER BY count(p) DESC");
    for (Object[] o : list) {
      if (o[0] == null) continue;
      final Lane lane = (o[0].toString()).toEnum(Lane.class);
      final int amount = ((Long) o[1]).intValue();
      if (lane != Lane.UNKNOWN) add(lane, player, amount);
    }
  }

  private void handleNoPlayers() {
    if (laneLineups.keySet().size() == 5) return;

    for (Player player : players) {
      final List<Object[]> list = ScoutingGameType.MATCHMADE.playerQuery(player, 180).performance("lane, count(p)", "GROUP BY lane ORDER BY count(p) DESC");
      for (Object[] o : list) {
        if (o[0] == null) continue;
        final Lane lane = (o[0].toString()).toEnum(Lane.class);
        if (laneLineups.containsKey(lane)) continue;

        final int amount = ((Long) o[1]).intValue();
        if (lane != Lane.UNKNOWN) add(lane, player, amount);
      }
    }
  }

  private void handleSelectedLineup() {
    for (Lane lane : Lane.values()) {
      if (lane.equals(Lane.UNKNOWN)) continue;

      final Lineup atPosition = LineupFactory.determineLineup(participator, lane);
      if (atPosition == null) continue;

      only(lane, atPosition.getPlayer());
    }
  }


  List<Lineup> handleLineup() {
    final List<Player> determineLineup = determineLineup();
    if (determineLineup.size() != 5) {
      log.warning("Kein Lineup für Team " + participator.getId());
      LogFiles.log("Kein Lineup für Team " + participator.getId());
    }
    if (determineLineup.isEmpty()) return List.of();
    return Arrays.stream(Lane.values()).filter(lane -> lane.ordinal() > 0)
        .map(lane -> determineLineup(lane, Iterables.get(determineLineup, lane.ordinal() - 1, null)))
        .toList();
  }

  private Lineup determineLineup(Lane lane, Player player) {
    Lineup lineup = LineupFactory.determineLineup(participator, lane);
    if (lineup != null) return lineup;

    lineup = LineupFactory.determineLineup(participator, player);
    if (lineup != null) {
      lineup.setLane(lane);
      return lineup;
    }

    final Lineup lineup1 = new Lineup(lane, player);
    participator.addLineup(lineup1);
    return lineup1;
  }

  private List<Player> determineLineup() {
    int totalAmount = Integer.MIN_VALUE;
    List<Player> bestLineup = new ArrayList<>();
    for (LaneGames topLineup : laneLineups.getOrDefault(Lane.TOP, new LaneLineup(Lane.TOP)).getPlayers()) {
      final Player topPlayer = topLineup.player();
      final int topAmount = topLineup.amount();

      for (LaneGames jglLineup : laneLineups.getOrDefault(Lane.JUNGLE, new LaneLineup(Lane.JUNGLE)).getPlayers()) {
        final Player jglPlayer = jglLineup.player();
        final int jglAmount = jglLineup.amount();

        for (LaneGames midLineup : laneLineups.getOrDefault(Lane.MIDDLE, new LaneLineup(Lane.MIDDLE)).getPlayers()) {
          final Player midPlayer = midLineup.player();
          final int midAmount = midLineup.amount();

          for (LaneGames botLineup : laneLineups.getOrDefault(Lane.BOTTOM, new LaneLineup(Lane.BOTTOM)).getPlayers()) {
            final Player botPlayer = botLineup.player();
            final int botAmount = botLineup.amount();

            for (LaneGames supLineup : laneLineups.getOrDefault(Lane.UTILITY, new LaneLineup(Lane.UTILITY)).getPlayers()) {
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
