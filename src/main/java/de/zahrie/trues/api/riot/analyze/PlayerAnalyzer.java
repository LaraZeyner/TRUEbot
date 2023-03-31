package de.zahrie.trues.api.riot.analyze;

import de.zahrie.trues.api.coverage.player.PlayerHandler;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import com.merakianalytics.orianna.types.common.Map;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.database.Database;
import org.joda.time.DateTime;

public record PlayerAnalyzer(Player player) {
  public void analyze() {
    boolean hasPlayedRanked = false;
    final Summoner summoner = Xayah.summonerWithPuuid(player.getPuuid()).get();
    final Time currentTime = Time.of();
    final MatchHistory matchHistory = Xayah.matchHistoryForSummoner(summoner)
        .withStartTime(new DateTime(player.getUpdated()))
        .withQueues(Queue.CUSTOM, Queue.CLASH, Queue.RANKED_SOLO, Queue.RANKED_FLEX, Queue.NORMAL, Queue.BLIND_PICK).get();
    for (Match match : matchHistory) {
      if (match.getParticipants().size() != 10) {
        continue;
      }
      if (!match.getMap().equals(Map.SUMMONERS_RIFT)) {
        continue;
      }
      final MatchAnalyzer matchAnalyzer = new MatchAnalyzer(match);
      final Game game = matchAnalyzer.analyze();
      if (game.getType().equals(GameType.RANKED_SOLO)) hasPlayedRanked = true;
      Database.save(game);
    }
    if (hasPlayedRanked) new PlayerHandler(null, player).updateElo();
    player.setUpdated(currentTime);
    Database.save(player);
    Database.connection().commit(null);
  }
}
