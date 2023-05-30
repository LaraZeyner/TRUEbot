package de.zahrie.trues.api.coverage.player;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.player.model.Division;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.Rank;
import de.zahrie.trues.api.coverage.player.model.RankFactory;
import de.zahrie.trues.api.riot.Zeri;
import lombok.Builder;
import lombok.Getter;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

@Getter
public class PlayerHandler extends PlayerModel implements Serializable {
  @Serial
  private static final long serialVersionUID = -3900511589414972005L;

  @Builder
  public PlayerHandler(String url, Player player) {
    super(url, player);
  }

  public void updateName() {
    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByPUUID(LeagueShard.EUW1, player.getPuuid());
    if (summoner != null) player.setSummonerName(summoner.getName());
  }

  public void updateElo() {
    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByPUUID(LeagueShard.EUW1, player.getPuuid());
    final LeagueEntry entry = summoner.getLeagueEntry().stream().filter(leagueEntry1 -> leagueEntry1.getQueueType().equals(GameQueueType.RANKED_SOLO_5X5)).findFirst().orElse(null);
    if (entry == null) return;

    final String tier = entry.getTier();
    final Division division = Division.valueOf(entry.getRank());
    final int leaguePoints = entry.getLeaguePoints();
    final int wins = entry.getWins();
    final int losses = entry.getLosses();
    RankFactory.updateRank(player, Rank.RankTier.valueOf(tier), division, (byte) leaguePoints, wins, losses);
  }
}
