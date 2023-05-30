package de.zahrie.trues.api.coverage.player.model;

import java.time.LocalDateTime;

import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.user.DiscordUser;

public class RankFactory {
  public static void updateRank(Player player, Rank.RankTier tier, Division division, byte points, int wins, int losses) {
    final PlayerRank rank = player.getRankInSeason();
    if (rank == null) new PlayerRank(player, tier, Division.valueOf(division.name()), points, wins, losses).create();

    if (rank == null || tier.ordinal() <= rank.getRank().tier().ordinal()) return;
    final DiscordUser discordUser = player.getDiscordUser();
    if (discordUser != null) discordUser.addGroup(DiscordGroup.VIP, LocalDateTime.now(), 7);
  }
}
