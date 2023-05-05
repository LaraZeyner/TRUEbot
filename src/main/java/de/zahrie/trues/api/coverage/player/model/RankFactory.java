package de.zahrie.trues.api.coverage.player.model;

import com.merakianalytics.orianna.types.common.Tier;

public class RankFactory {
  public static void updateRank(PlayerBase player, Tier tier, Division division, byte points, int wins, int losses) {
    final Rank rank = player.getRankInSeason();
    if (rank == null)
      new Rank(player, tier, de.zahrie.trues.api.coverage.player.model.Division.valueOf(division.name()), points, wins, losses).create();
  }
}
