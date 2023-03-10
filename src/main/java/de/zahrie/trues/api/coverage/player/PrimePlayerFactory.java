package de.zahrie.trues.api.coverage.player;

import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.logger.Logger;
import org.jetbrains.annotations.Nullable;

public final class PrimePlayerFactory {

  @Nullable
  public static PrimePlayer getPlayer(int primeId) {
    return Database.Find.find(PrimePlayer.class, new String[]{"primeId"}, new Object[]{primeId}, "fromPrimeId");
  }

  @Nullable
  public static PrimePlayer getPrimePlayer(int primeId, String summonerName) {
    final PrimePlayer player = getPlayer(primeId);
    if (player != null) {
      updatePrmAccount(player, summonerName);
      return player;
    }

    final PrimePlayer p = determinePlayer(summonerName, primeId);
    if (p != null) {
      p.setPrmUserId(primeId);
      Database.save(p);
    }
    return p;
  }

  private static void updatePrmAccount(PrimePlayer player, String summonerName) {
    final Summoner summoner = Xayah.summonerNamed(summonerName).get();
    final String puuid = summoner.getPuuid();
    if (!player.getPuuid().equals(puuid) && puuid != null) {
      player.setPuuid(puuid);
      player.setSummonerName(summoner.getName());
      Database.save(player);
    }
  }

  @Nullable
  private static PrimePlayer determinePlayer(String summonerName, int primeId) {
    final String puuid = Xayah.summonerNamed(summonerName).get().getPuuid();
    if (puuid != null) {
      final PrimePlayer primePlayer = determineExistingPlayerFromPuuid(puuid);
      return primePlayer != null ? primePlayer : new PrimePlayer(puuid, summonerName, primeId);
    }
    return performNoPuuid(summonerName);
  }

  @Nullable
  private static PrimePlayer performNoPuuid(String summonerName) {
    final PrimePlayer primePlayer = determineExistingPlayerFromName(summonerName);
    if (primePlayer == null) {
      Logger.getLogger("Player").attention("Der Spieler existiert nicht");
      return null;
    }

    new PlayerHandler(null, primePlayer).updateName();
    Database.save(primePlayer);

    return determineExistingPlayerFromName(summonerName);
  }

  private static PrimePlayer determineExistingPlayerFromName(String summonerName) {
    return summonerName == null ? null : Database.Find.find(PrimePlayer.class, new String[]{"name"}, new Object[]{summonerName}, "fromName");
  }

  private static PrimePlayer determineExistingPlayerFromPuuid(String puuid) {
    return puuid == null ? null : Database.Find.find(PrimePlayer.class, new String[]{"puuid"}, new Object[]{puuid}, "fromPuuid");
  }

}
