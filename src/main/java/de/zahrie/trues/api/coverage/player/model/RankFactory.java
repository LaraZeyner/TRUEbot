package de.zahrie.trues.api.coverage.player.model;

import de.zahrie.trues.api.riot.xayah.types.common.Division;
import de.zahrie.trues.api.riot.xayah.types.common.Tier;
import de.zahrie.trues.database.Database;

/**
 * Created by Lara on 23.02.2023 for TRUEbot
 */
public class RankFactory {

  public static void updateRank(Player player, Tier tier, Division division, byte points, int wins, int losses) {
    Rank rank = Database.Find.find(Rank.class, new String[]{"player"}, new Object[]{player}, "fromPlayer");
    if (rank == null) {
      rank = new Rank(player, tier, division, points, wins, losses);
    } else {
      rank.setTier(tier);
      rank.setDivision(division);
      rank.setPoints(points);
      rank.setWins(wins);
      rank.setLosses(losses);
    }
    rank.setMmr(RankFactory.determineMMR(tier, division, points));
    Database.save(rank);
  }
  private static int determineMMR(Tier tier, Division division, int points) {
    int mmr = points;
    mmr += (tier.getLevel() - 1) * 400;
    mmr += division.getLevel() * 100;
    return mmr;
  }
}
