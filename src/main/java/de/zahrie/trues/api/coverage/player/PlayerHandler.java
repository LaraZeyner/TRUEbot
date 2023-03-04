package de.zahrie.trues.api.coverage.player;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.riot.xayah.types.common.Division;
import de.zahrie.trues.api.riot.xayah.types.common.Queue;
import de.zahrie.trues.api.riot.xayah.types.common.Tier;
import de.zahrie.trues.api.riot.xayah.types.core.league.LeagueEntry;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.player.model.RankFactory;
import de.zahrie.trues.database.Database;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
@Getter
public class PlayerHandler extends PlayerModel implements Serializable {
  @Serial
  private static final long serialVersionUID = -3900511589414972005L;

  @Builder
  public PlayerHandler(@Deprecated String url, PrimePlayer player) {
    super(url, player);
  }

  public void update() {
    updateName();
    if (player.getTeam().getRefresh().after(new Date())) {
      updateElo();
    }
    Database.save(player);
  }

  public void updateName() {
    final Summoner summoner = Xayah.summonerWithPuuid(player.getPuuid()).get();
    if (summoner != null) {
      player.setSummonerName(summoner.getName());
    }
  }

  private void updateElo() {
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
    if (wins + losses > 50 || player.getRank() == null) {
      RankFactory.updateRank(player, tier, division, (byte) leaguePoints, wins, losses);
    }
  }

}
