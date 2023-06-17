package de.zahrie.trues.api.coverage.participator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.participator.model.Lineup;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.Rank;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.collections.SortedList;
import de.zahrie.trues.api.riot.performance.Lane;
import de.zahrie.trues.api.scouting.PlayerAnalyzer;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.api.scouting.TeamAnalyzer;
import de.zahrie.trues.util.Util;
import lombok.Getter;

@Getter
public class TeamLineup extends TeamLineupBase {
  private final ScoutingGameType gameType;
  private final int days;
  private List<Lineup> lineup;
  private Map<Lane, Map<Player, Integer>> games;

  public TeamLineup(Participator participator, ScoutingGameType gameType, int days) {
    super(participator);
    this.gameType = gameType;
    this.days = days;
  }

  public void updateLineups() {
    this.games = null;
    this.lineup = null;
    if (participator.getMatch() == null) this.storedLineups = new SortedList<>();
    if (participator.getMatch() == null || (participator.getMatch().getStatus().equals(EventStatus.PLAYED) && storedLineups != null)) return;

    this.storedLineups = new Query<>(Lineup.class).where("coverage_team", participator).entityList();
  }

  public void updateMMR() {
    final int mmr = (int) storedLineups.stream().mapToInt(l -> l.getPlayer().getRanks().getLastRelevant().getRank().getMMR()).average().orElse(-1);
    participator.setMmr(mmr == -1 ? null : mmr);
  }

  public Rank getAverageRank() {
    return Rank.fromMMR(participator.getMmr() == -1 ? Util.avoidNull(participator.getTeam(), 0, team -> Util.avoidNull(team.getLastMMR(), 0)) : participator.getMmr());
  }

  public List<Lineup> getLineup() {
    if (lineup == null) {
      this.games = determinePlayersOnLanes();
      this.lineup = handleLineup();
    }
    return lineup;
  }

  private Map<Lane, Map<Player, Integer>> determinePlayersOnLanes() {
    final Map<Lane, Map<Player, Integer>> map = new HashMap<>();
    for (final Lane lane : Lane.ITERATE) {
      Map<Player, Integer> players = determinePlayersOnLane(lane);
      if (players.values().stream().mapToInt(Integer::intValue).sum() == 0) {
        players = new TeamLineup(participator, ScoutingGameType.MATCHMADE, days).determinePlayersOnLane(lane);
      }
      map.put(lane, players);
    }
    return map;
  }

  private Map<Player, Integer> determinePlayersOnLane(Lane lane) {
    final Map<Player, Integer> laneData = new TeamAnalyzer(participator).getLane(lane);
    return getValidPlayers(lane).stream().collect(Collectors.toMap(validPlayer -> validPlayer, validPlayer -> laneData.getOrDefault(validPlayer, new PlayerAnalyzer(validPlayer, gameType, days).getLane(lane)), (a, b) -> b, LinkedHashMap::new));
  }

  public List<LaneGames> getPlayersOnLane(Lane lane) {
    final Lineup storedLineup = getStoredLineup(lane);
    if (storedLineup != null) return List.of(new LaneGames(storedLineup.getPlayer(), storedLineup.getPlayer().analyze(gameType, days).getLane(lane)));

    final Map<Player, Integer> lane1 = new TeamAnalyzer(participator).getLane(lane);
    // final List<Player> players = getStoredLineups().stream().filter(lineup1 -> lineup1.getLane() != Lane.UNKNOWN).map(Lineup::getPlayer).toList();
    return lane1.entrySet().stream().map(entry -> new LaneGames(entry.getKey(), entry.getValue()))
        //.filter(laneGames -> !players.contains(laneGames.player()))
        .toList();
  }

  private List<Lineup> handleLineup() {
    final Player[] players = determineLineup();
    return new SortedList<>(Lane.ITERATE.stream().map(lane -> determineLineup(lane, players[lane.ordinal()-1])).toList());
  }

  private Lineup determineLineup(Lane lane, Player player) {
    final Lineup lineup = getStoredLineup(lane);
    return lineup != null && lineup.getPlayer().equals(player) ? lineup : new Lineup(participator, player, lane);
  }

  private Player[] determineLineup() {
    int totalAmount = Integer.MIN_VALUE;
    Player[] bestLineup = new Player[]{null, null, null, null, null};
    for (Map.Entry<Player, Integer> topLineup : games.getOrDefault(Lane.TOP, new HashMap<>()).entrySet()) {
      final Player topPlayer = topLineup.getKey();
      final int topAmount = topLineup.getValue();

      for (Map.Entry<Player, Integer> jglLineup : games.getOrDefault(Lane.JUNGLE, new HashMap<>()).entrySet()) {
        final Player jglPlayer = jglLineup.getKey();
        final int jglAmount = jglLineup.getValue();

        for (Map.Entry<Player, Integer> midLineup : games.getOrDefault(Lane.MIDDLE, new HashMap<>()).entrySet()) {
          final Player midPlayer = midLineup.getKey();
          final int midAmount = midLineup.getValue();

          for (Map.Entry<Player, Integer> botLineup : games.getOrDefault(Lane.BOTTOM, new HashMap<>()).entrySet()) {
            final Player botPlayer = botLineup.getKey();
            final int botAmount = botLineup.getValue();

            for (Map.Entry<Player, Integer> supLineup : games.getOrDefault(Lane.UTILITY, new HashMap<>()).entrySet()) {
              final Player supPlayer = supLineup.getKey();
              final int supAmount = supLineup.getValue();

              final int amount = topAmount + jglAmount + midAmount + botAmount + supAmount;
              if (amount > totalAmount) {
                totalAmount = amount;
                bestLineup = List.of(topPlayer, jglPlayer, midPlayer, botPlayer, supPlayer).toArray(Player[]::new);
              }
            }
          }
        }
      }
    }
    return bestLineup;
  }

  public List<Player> getValidPlayers() {
    final var players = new SortedList<>(participator.getTeamLineup().getStoredLineups().stream().map(Lineup::getPlayer));
    if (players.size() < 5) {
      final Team team = participator.getTeam();
      if (team != null) players.addAll(team.getPlayers());
    }
    return players;
  }

  public List<Player> getSetPlayers() {
    return new Query<>(Lineup.class).join(new JoinQuery<>(Lineup.class, Player.class)).where("coverage_team", participator)
        .convertList(Player.class);
  }

  public List<Player> getValidPlayers(Lane lane) {
    final Lineup storedLineup = getStoredLineup(lane);
    if (storedLineup != null) return List.of(storedLineup.getPlayer());

    final List<Player> players = getStoredLineups().stream().filter(lineup1 -> lineup1.getLane() != Lane.UNKNOWN).map(Lineup::getPlayer).toList();
    final List<Player> validPlayers = new SortedList<>(getValidPlayers());
    validPlayers.removeIf(players::contains);
    return validPlayers;
  }

  public record LaneGames(Player player, int amount) { }
}
