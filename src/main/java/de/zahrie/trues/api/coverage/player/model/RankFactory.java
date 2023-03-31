package de.zahrie.trues.api.coverage.player.model;

import java.util.List;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.database.Database;

public class RankFactory {

  public static Rank getRank(Player player) {
    final List<Rank> ranks = Database.Find.findList(Rank.class, new String[]{"player"}, new Object[]{player}, "fromPlayer");
    for (Rank rank : ranks) {
      if (rank.getSeason().getId() < SeasonFactory.getLastSeason().getId() - 2) return null;
      if (rank.getWinrate().getGames() >= 50) return rank;
    }
    for (Rank rank : ranks) {
      if (rank.getSeason().getId() < SeasonFactory.getLastSeason().getId() - 2) return null;
      if (rank.getWinrate().getGames() >= 10) return rank;
    }
    return null;
  }

  public static void updateRank(Player player, Tier tier, Division division, byte points, int wins, int losses) {
    Rank rank = Database.Find.find(Rank.class, new String[]{"player", "season"}, new Object[]{player, SeasonFactory.getLastSeason()}, "fromPlayerOnSeason");
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
    mmr += (de.zahrie.trues.api.coverage.player.model.Tier.fromTier(tier).getLevel() - 1) * 400;
    mmr += de.zahrie.trues.api.coverage.player.model.Division.fromDivision(division).getLevel() * 100;
    return mmr;
  }
}
