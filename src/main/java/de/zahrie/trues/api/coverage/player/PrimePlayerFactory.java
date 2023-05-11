package de.zahrie.trues.api.coverage.player;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import de.zahrie.trues.api.coverage.player.model.PRMPlayer;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.Xayah;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
public final class PrimePlayerFactory {

  @Nullable
  public static PRMPlayer getPlayer(int primeId) {
    final PRMPlayer prmPlayer = new Query<>(PRMPlayer.class).where("prm_id", primeId).entity();
    if (prmPlayer != null) return prmPlayer;
    return new PlayerLoader(primeId).handleMissingPlayer();
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
    }
    return p;
  }

  private static void updatePrmAccount(PRMPlayer player, String summonerName) {
    final Summoner summoner = Xayah.summonerNamed(summonerName).get();
    final String puuid = summoner.getPuuid();
    if (!player.getPuuid().equals(puuid) && puuid != null) {
      player.setPuuidAndName(puuid, summoner.getName());
    }
  }

  @Nullable
  private static PRMPlayer determinePlayer(String summonerName, int primeId) {
    final String puuid = Xayah.summonerNamed(summonerName).get().getPuuid();
    if (puuid != null) {
      return new PRMPlayer(puuid, summonerName, primeId).create();
    }
    final PRMPlayer prmPlayer = (PRMPlayer) performNoPuuid(summonerName);
    if (prmPlayer != null) prmPlayer.setPrmUserId(primeId);
    return prmPlayer;
  }

  @Nullable
  private static Player performNoPuuid(String summonerName) {
    final Player player = determineExistingPlayerFromName(summonerName);
    if (player == null) {
      log.config("Der Spieler existiert nicht");
      return null;
    }

    new PlayerHandler(null, player).updateName();
    return determineExistingPlayerFromName(summonerName);
  }

  private static Player determineExistingPlayerFromName(String summonerName) {
    return summonerName == null ? null : new Query<>(Player.class).where("lol_name", summonerName).entity();
  }

  private static Player determineExistingPlayerFromPuuid(String puuid) {
    return puuid == null ? null : new Query<>(Player.class).where("lol_puuid", puuid).entity();
  }
}
