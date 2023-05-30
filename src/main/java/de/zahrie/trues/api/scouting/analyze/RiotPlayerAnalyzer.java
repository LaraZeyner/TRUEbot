package de.zahrie.trues.api.scouting.analyze;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import de.zahrie.trues.api.coverage.player.PlayerHandler;
import de.zahrie.trues.api.coverage.player.model.Player;
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
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.basic.constants.types.lol.MapType;
import no.stelar7.api.r4j.basic.constants.types.lol.MatchlistMatchType;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public record RiotPlayerAnalyzer(Player player) {
  private static final List<Player> fullyAnalyzedPlayers = new SortedList<>();

  public static void reset() {
    fullyAnalyzedPlayers.clear();
  }

  public void analyze(boolean onlyClashPlus) {
    analyze(onlyClashPlus, false);
  }

  public void analyze(boolean onlyClashPlus, boolean force) {
    if (!onlyClashPlus && fullyAnalyzedPlayers.contains(player) && !force) return;

    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByPUUID(LeagueShard.EUW1, player.getPuuid());
    player.setSummonerName(summoner.getName());
    final LocalDateTime currentTime = LocalDateTime.now();

    if (new Query<>(Performance.class, "SELECT performance.* FROM performance " +
        "JOIN team_perf tp on performance.t_perf = tp.team_perf_id JOIN game g on tp.game = g.game_id " +
        "WHERE g.game_type <= ?").entity(List.of(GameType.CUSTOM)) == null) {
      final MatchHistoryBuilder historyBuilder = new MatchHistoryBuilder(summoner, LocalDateTime.MIN).with(GameQueueType.CUSTOM);
      analyze(historyBuilder);
    }
    final MatchHistoryBuilder historyBuilder = new MatchHistoryBuilder(summoner, player.getUpdated())
        .with(GameQueueType.CUSTOM).with(GameQueueType.CLASH);
    if (!onlyClashPlus) historyBuilder.with(MatchlistMatchType.NORMAL).with(MatchlistMatchType.RANKED);
    if (analyze(historyBuilder)) new PlayerHandler(null, player).updateElo();
    player.setUpdated(currentTime);
    AnalyzeManager.delete(player);
    Database.connection().commit(null);

    if (!onlyClashPlus) fullyAnalyzedPlayers.add(player);
  }

  private boolean analyze(MatchHistoryBuilder history) {
    long start = System.currentTimeMillis();
    boolean hasPlayedRanked = false;
    for (String matchId : new HashSet<>(history.get())) {
      final LOLMatch match = Zeri.get().getMatchAPI().getMatch(RegionShard.EUROPE, matchId);
      if (match.getParticipants().size() != 10) continue;
      if (!match.getMap().equals(MapType.SUMMONERS_RIFT)) continue;
      if (List.of(GameQueueType.BOT_5X5_INTRO, GameQueueType.BOT_5X5_BEGINNER, GameQueueType.BOT_5X5_INTERMEDIATE,
          GameQueueType.ALL_RANDOM_URF, GameQueueType.ULTBOOK).contains(match.getQueue())) continue;

      final RiotMatchAnalyzer matchAnalyzer = new RiotMatchAnalyzer(player, match);
      final Game game = matchAnalyzer.analyze();
      if (game != null && game.getType().equals(GameType.RANKED_SOLO)) hasPlayedRanked = true;
    }
    if (history.get().size() > 100)
      System.out.println(player.getSummonerName() + " -> " + (System.currentTimeMillis() - start) / 1000.0 + " für " + history.get().size());
    return hasPlayedRanked;
  }
}
