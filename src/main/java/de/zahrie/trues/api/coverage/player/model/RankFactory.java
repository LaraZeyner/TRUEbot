package de.zahrie.trues.api.coverage.player.model;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.database.Database;

public class RankFactory {
  public static void updateRank(Player player, Tier tier, Division division, byte points, int wins, int losses) {
    final Rank rank = player.getRankInSeason();
    if (rank == null) {
      Rank.build(player, tier, division, points, wins, losses);
      return;
    }
    rank.setTier(AbstractRank.RankTier.valueOf(tier.name()));
    rank.setDivision(division);
    rank.setPoints(points);
    rank.setWins(wins);
    rank.setLosses(losses);
    Database.update(rank);
  }
}
