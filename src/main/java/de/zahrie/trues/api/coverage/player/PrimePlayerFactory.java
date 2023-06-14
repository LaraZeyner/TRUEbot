package de.zahrie.trues.api.coverage.player;

import de.zahrie.trues.api.coverage.player.model.PRMPlayer;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.Zeri;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(Util.class)
public final class PrimePlayerFactory {
  /**
   * @return Spieler von der Primeid
   */
  @Nullable
  public static PRMPlayer getPlayer(int playerId) {
    final PRMPlayer prmPlayer = new Query<>(PRMPlayer.class).where("prm_id", playerId).entity();
    if (prmPlayer != null) return prmPlayer;

    return PlayerLoader.create(playerId).avoidNull(PlayerLoader::getPlayer);
  }


  @Nullable
  public static PRMPlayer getPrimePlayer(int primeId, String summonerName) {
    PRMPlayer player = new Query<>(PRMPlayer.class).where("prm_id", primeId).entity();
    if (summonerName == null) return player;

    if (player != null) {
      updatePrmAccount(player, summonerName);
    } else {
      player = createPlayer(summonerName, primeId);
      if (player != null) player.setPrmUserId(primeId);
    }
    return player;
  }

  private static void updatePrmAccount(@NonNull PRMPlayer player, @NonNull String summonerName) {
    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, summonerName);
    final String puuid = summoner == null ? player.getPuuid() : summoner.getPUUID();

    final Player lolPuuid = new Query<>(Player.class).where("lol_puuid", puuid)
        .and(Condition.Comparer.NOT_EQUAL, "player_id", player.getId()).entity();
    if (lolPuuid != null) {
      lolPuuid.setPuuidAndName(null, null);
    }

    if (!player.getPuuid().equals(puuid) && puuid != null) {
      player.setPuuidAndName(puuid, summoner.getName());
    }
  }

  @Nullable
  private static PRMPlayer createPlayer(String summonerName, int primeId) {
    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, summonerName);
    if (summoner != null) {
      final String puuid = summoner.getPUUID();
      if (puuid != null) return new PRMPlayer(summonerName, puuid, primeId).create();
    }

    final PRMPlayer prmPlayer = (PRMPlayer) performNoPuuid(summonerName);
    if (prmPlayer != null) {
      prmPlayer.setPrmUserId(primeId);
    }
    return prmPlayer;
  }

  @Nullable
  private static Player performNoPuuid(String summonerName) {
    final Player player = determineExistingPlayerFromName(summonerName);
    if (player == null) return null;

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
