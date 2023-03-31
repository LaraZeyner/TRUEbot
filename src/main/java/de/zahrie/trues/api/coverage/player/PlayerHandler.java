package de.zahrie.trues.api.coverage.player;

import java.io.Serial;
import java.io.Serializable;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Tier;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.RankFactory;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.database.Database;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlayerHandler extends PlayerModel implements Serializable {
  @Serial
  private static final long serialVersionUID = -3900511589414972005L;

  @Builder
  public PlayerHandler(@Deprecated String url, Player player) {
    super(url, player);
  }

  public void update() {
    updateName();
    Database.save(player);
  }

  public void updateName() {
    final Summoner summoner = Xayah.summonerWithPuuid(player.getPuuid()).get();
    if (summoner != null) {
      player.setSummonerName(summoner.getName());
    }
  }

  public void updateElo() {
    final Summoner summoner = Xayah.summonerWithPuuid(player.getPuuid()).get();
    final LeagueEntry entry = summoner.getLeaguePosition(Queue.RANKED_SOLO);
    if (entry == null) {
      return;
    }

    final Tier tier = entry.getTier();
    final Division division = entry.getDivision();
    final int leaguePoints = entry.getLeaguePoints();
    final int wins = entry.getWins();
    final int losses = entry.getLosses();
    RankFactory.updateRank(player, tier, division, (byte) leaguePoints, wins, losses);
  }

}
