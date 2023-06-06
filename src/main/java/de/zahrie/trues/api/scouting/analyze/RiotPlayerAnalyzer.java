package de.zahrie.trues.api.scouting.analyze;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.zahrie.trues.api.coverage.player.PlayerHandler;
import de.zahrie.trues.api.coverage.player.model.LoaderGameType;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.Database;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.collections.SortedList;
import de.zahrie.trues.api.riot.Zeri;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.riot.match.MatchHistoryBuilder;
import de.zahrie.trues.api.riot.match.RiotMatchAnalyzer;
import de.zahrie.trues.api.riot.performance.Performance;
import de.zahrie.trues.api.scouting.AnalyzeManager;
import de.zahrie.trues.util.Util;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.basic.constants.types.lol.MapType;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public record RiotPlayerAnalyzer(Player player) {
  private static final List<Integer> fullyAnalyzedPlayers = Collections.synchronizedList(new SortedList<>());
  private static final List<Integer> currentPlayers = Collections.synchronizedList(new SortedList<>());

  public static void reset() {
    fullyAnalyzedPlayers.clear();
  }

  public void analyze(LoaderGameType gameType, boolean force) {
    if (currentPlayers.contains(player.getId())) return;
    if (gameType.equals(LoaderGameType.MATCHMADE) && fullyAnalyzedPlayers.contains(player.getId()) && !force) return;

    currentPlayers.add(player.getId());
    fullyAnalyzedPlayers.add(player.getId());

    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByPUUID(LeagueShard.EUW1, player.getPuuid());
    player.setSummonerName(summoner.getName());
    final LocalDateTime currentTime = LocalDateTime.now();

    if (new Query<>(Performance.class, "SELECT performance.* FROM performance " +
        "JOIN team_perf tp on performance.t_perf = tp.team_perf_id JOIN game g on tp.game = g.game_id " +
        "WHERE g.game_type <= ?").entity(List.of(GameType.CUSTOM)) == null) {
      final MatchHistoryBuilder historyBuilder = new MatchHistoryBuilder(summoner, LocalDateTime.MIN).with(GameQueueType.CUSTOM);
      analyze(historyBuilder.get(), gameType);
    }

    final var historyBuilder = gameType.getMatchHistory(summoner, player);
    if (analyze(historyBuilder, gameType)) new PlayerHandler(null, player).updateElo();
    else player.getRanks().createRank();
    AnalyzeManager.delete(player);

    if (gameType.equals(LoaderGameType.MATCHMADE)) player.setUpdated(currentTime);
    /*final SortedList<Integer> pls = new SortedList<>(currentPlayers);
    pls.remove(player.getId());
    currentPlayers = Collections.synchronizedList(pls);*/
    currentPlayers.remove(Integer.valueOf(player.getId()));
    Database.connection().commit(null);
  }

  private boolean analyze(List<String> history, LoaderGameType gameType) {
    final long start = System.currentTimeMillis();
    boolean hasPlayedRanked = false;
    for (String matchId : new HashSet<>(history)) {
      final LOLMatch match = Zeri.get().getMatchAPI().getMatch(RegionShard.EUROPE, matchId);
      if (match.getParticipants().size() != 10) continue;
      if (!match.getMap().equals(MapType.SUMMONERS_RIFT)) continue;
      if (List.of(GameQueueType.BOT_5X5_INTRO, GameQueueType.BOT_5X5_BEGINNER, GameQueueType.BOT_5X5_INTERMEDIATE,
          GameQueueType.ALL_RANDOM_URF, GameQueueType.ULTBOOK).contains(match.getQueue())) continue;

      final RiotMatchAnalyzer matchAnalyzer = new RiotMatchAnalyzer(player, match);
      final Game game = matchAnalyzer.analyze();
      if (game != null && game.getType().equals(GameType.RANKED_SOLO)) hasPlayedRanked = true;
    }

    if (gameType.equals(LoaderGameType.MATCHMADE)/* && history.size() > 20*/) {
      System.out.println(player.getSummonerName() + " (" + Util.avoidNull(player.getTeam(), "null", Team::getName) + ") -> " + (System.currentTimeMillis() - start) / 1000.0 + " für " + history.size());
    }
    return hasPlayedRanked;
  }
}
