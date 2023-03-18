package de.zahrie.trues.api.riot.analyze;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.xayah.types.common.Map;
import de.zahrie.trues.api.riot.xayah.types.common.Queue;
import de.zahrie.trues.api.riot.xayah.types.core.match.Match;
import de.zahrie.trues.api.riot.xayah.types.core.match.MatchHistory;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import de.zahrie.trues.database.Database;

public record PlayerAnalyzer(Player player) {
  public void analyze() {
    final Summoner summoner = Xayah.summonerWithPuuid(player.getPuuid()).get();
    final MatchHistory matchHistory = Xayah.matchHistoryForSummoner(summoner).withStartTime(player.getUpdated()).withQueues(Queue.CUSTOM, Queue.CLASH, Queue.RANKED_SOLO, Queue.RANKED_FLEX, Queue.NORMAL, Queue.BLIND_PICK).get();
    for (final Match match : matchHistory) {
      if (match.getParticipants().size() != 10) {
        continue;
      }
      if (!match.getMap().equals(Map.SUMMONERS_RIFT)) {
        continue;
      }
      final MatchAnalyzer matchAnalyzer = new MatchAnalyzer(match);
      final Game game = matchAnalyzer.analyze();
      Database.save(game);
    }

    Database.connection().commit();
  }
}
