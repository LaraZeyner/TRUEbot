package de.zahrie.trues.api.coverage.player.model;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;

public class RankFactory {
  public static void updateRank(Player player, Tier tier, Division division, byte points, int wins, int losses) {
    Rank rank = QueryBuilder.hql(Rank.class, "FROM Rank WHERE player = " + player + " AND season = " + SeasonFactory.getLastPRMSeason()).single();
    if (rank == null) {
      rank = new Rank(player, tier, division, points, wins, losses);
    } else {
      rank.setTier(AbstractRank.RankTier.valueOf(tier.name()));
      rank.setDivision(division);
      rank.setPoints(points);
      rank.setWins(wins);
      rank.setLosses(losses);
    }
    Database.save(rank);
  }
}
