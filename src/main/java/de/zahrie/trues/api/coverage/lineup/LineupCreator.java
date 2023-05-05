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
import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.StringUtils;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.java.Log;

@Getter
@ExtensionMethod(StringUtils.class)
@Log
public class LineupCreator extends LineupCreatorBase {
  private final Participator participator;
  private final Set<PlayerBase> players;
  private final Set<PlayerBase> blacklist = new LinkedHashSet<>();

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

  private Set<PlayerBase> determinePlayers() {
    final Set<PlayerBase> players = participator.getLineups().stream().map(Lineup::getPlayer).collect(Collectors.toSet());
    if (participator.getLineups().size() == 5) return players;

    players.addAll(participator.getTeam().getPlayers());
    return players;
  }

  private void handleLineup(PlayerBase player, ScoutingGameType gameType, int days) {
    final List<Object[]> list = gameType.playerQuery(player, days).performance().get("lane", Lane.class).get("count(performance_id)", Integer.class).groupBy("lane").descending("count(performance_id)").list();
    for (Object[] o : list) {
      if (o[0] == null) continue;
      final Lane lane = (o[0].toString()).toEnum(Lane.class);
      final int amount = ((Long) o[1]).intValue();
      if (lane != Lane.UNKNOWN) add(lane, player, amount);
    }
  }

  private void handleNoPlayers() {
    if (laneLineups.keySet().size() == 5) return;

    for (PlayerBase player : players) {
      final List<Object[]> list = ScoutingGameType.MATCHMADE.playerQuery(player, 180).performance().get("lane", Lane.class).get("count(performance_id)", Integer.class).groupBy("lane").descending("count(performance_id)").list();
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
    final List<PlayerBase> determineLineup = determineLineup();
    if (determineLineup.size() != 5) {
      log.warning("Kein Lineup für Team " + participator.getId());
    }
    if (determineLineup.isEmpty()) return List.of();
    return Arrays.stream(Lane.values()).filter(lane -> lane.ordinal() > 0)
        .map(lane -> determineLineup(lane, Iterables.get(determineLineup, lane.ordinal() - 1, null)))
        .toList();
  }

  private Lineup determineLineup(Lane lane, PlayerBase player) {
    Lineup lineup = LineupFactory.determineLineup(participator, lane);
    if (lineup != null && lane != Lane.UNKNOWN) return lineup;

    lineup = LineupFactory.determineLineup(participator, player);
    if (lineup != null) {
      lineup.setLane(lane);
      return lineup;
    }

    return new Lineup(participator, player, lane);
  }

  private List<PlayerBase> determineLineup() {
    int totalAmount = Integer.MIN_VALUE;
    List<PlayerBase> bestLineup = new ArrayList<>();
    for (LaneGames topLineup : laneLineups.getOrDefault(Lane.TOP, new LaneLineup(Lane.TOP)).getPlayers()) {
      final PlayerBase topPlayer = topLineup.player();
      final int topAmount = topLineup.amount();

      for (LaneGames jglLineup : laneLineups.getOrDefault(Lane.JUNGLE, new LaneLineup(Lane.JUNGLE)).getPlayers()) {
        final PlayerBase jglPlayer = jglLineup.player();
        final int jglAmount = jglLineup.amount();

        for (LaneGames midLineup : laneLineups.getOrDefault(Lane.MIDDLE, new LaneLineup(Lane.MIDDLE)).getPlayers()) {
          final PlayerBase midPlayer = midLineup.player();
          final int midAmount = midLineup.amount();

          for (LaneGames botLineup : laneLineups.getOrDefault(Lane.BOTTOM, new LaneLineup(Lane.BOTTOM)).getPlayers()) {
            final PlayerBase botPlayer = botLineup.player();
            final int botAmount = botLineup.amount();

            for (LaneGames supLineup : laneLineups.getOrDefault(Lane.UTILITY, new LaneLineup(Lane.UTILITY)).getPlayers()) {
              final PlayerBase supPlayer = supLineup.player();
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
