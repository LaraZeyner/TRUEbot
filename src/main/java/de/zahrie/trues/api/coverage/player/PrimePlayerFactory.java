package de.zahrie.trues.api.coverage.player;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import de.zahrie.trues.api.coverage.player.model.PRMPlayer;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.util.Util;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
public final class PrimePlayerFactory {

  @Nullable
  public static PRMPlayer getPlayer(int primeId) {
    return QueryBuilder.hql(PRMPlayer.class, "FROM PRMPlayer WHERE prmUserId = " + primeId).single();
  }

  @Nullable
  public static PRMPlayer getPrimePlayer(int primeId, String summonerName) {
    final PRMPlayer player = getPlayer(primeId);
    if (player != null) {
      updatePrmAccount(player, summonerName);
      return player;
    }

    final PRMPlayer p = determinePlayer(summonerName, primeId);
    if (p != null) {
      p.setPrmUserId(primeId);
      Database.save(p);
    }
    return p;
  }

  private static void updatePrmAccount(PRMPlayer player, String summonerName) {
    final Summoner summoner = Xayah.summonerNamed(summonerName).get();
    final String puuid = summoner.getPuuid();
    if (!player.getPuuid().equals(puuid) && puuid != null) {
      player.setPuuid(puuid);
      player.setSummonerName(summoner.getName());
      Database.save(player);
    }
  }

  @Nullable
  private static PRMPlayer determinePlayer(String summonerName, int primeId) {
    final String puuid = Xayah.summonerNamed(summonerName).get().getPuuid();
    if (puuid != null) {
      final PRMPlayer PRMPlayer = determineExistingPlayerFromPuuid(puuid);
      return PRMPlayer != null ? PRMPlayer : new PRMPlayer(puuid, summonerName, primeId);
    }
    return performNoPuuid(summonerName);
  }

  @Nullable
  private static PRMPlayer performNoPuuid(String summonerName) {
    final PRMPlayer PRMPlayer = determineExistingPlayerFromName(summonerName);
    if (PRMPlayer == null) {
      log.config("Der Spieler existiert nicht");
      return null;
    }

    new PlayerHandler(null, PRMPlayer).updateName();
    Database.save(PRMPlayer);

    return determineExistingPlayerFromName(summonerName);
  }

  private static PRMPlayer determineExistingPlayerFromName(String summonerName) {
    return Util.avoidNull(summonerName, null,
        nameStr -> QueryBuilder.hql(PRMPlayer.class, "FROM PRMPlayer WHERE summonerName = " + nameStr).single());
  }

  private static PRMPlayer determineExistingPlayerFromPuuid(String puuid) {
    return Util.avoidNull(puuid, null,
        puuidStr -> QueryBuilder.hql(PRMPlayer.class, "FROM PRMPlayer WHERE puuid = " + puuidStr).single());
  }

}
