package de.zahrie.trues.api.coverage.player;

import java.io.Serial;
import java.io.Serializable;

import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Tier;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import de.zahrie.trues.api.coverage.player.model.Division;
import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.coverage.player.model.RankFactory;
import de.zahrie.trues.api.riot.Xayah;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlayerHandler extends PlayerModel implements Serializable {
  @Serial
  private static final long serialVersionUID = -3900511589414972005L;

  @Builder
  public PlayerHandler(String url, PlayerBase player) {
    super(url, player);
  }

  public void update() {
    updateName();
  }

  public void updateName() {
    final Summoner summoner = Xayah.summonerWithPuuid(player.getPuuid()).get();
    if (summoner != null) player.setSummonerName(summoner.getName());
  }

  public void updateElo() {
    final Summoner summoner = Xayah.summonerWithPuuid(player.getPuuid()).get();
    final LeagueEntry entry = summoner.getLeaguePosition(Queue.RANKED_SOLO);
    if (entry == null) {
      return;
    }

    final Tier tier = entry.getTier();
    final Division division = Division.valueOf(entry.getDivision().name());
    final int leaguePoints = entry.getLeaguePoints();
    final int wins = entry.getWins();
    final int losses = entry.getLosses();
    RankFactory.updateRank(player, tier, division, (byte) leaguePoints, wins, losses);
  }

}
