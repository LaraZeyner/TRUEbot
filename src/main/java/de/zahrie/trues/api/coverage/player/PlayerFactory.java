package de.zahrie.trues.api.coverage.player;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import de.zahrie.trues.database.Database;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
public final class PlayerFactory {
  public static Player findPlayer(String puuid) {
    return puuid == null ? null : Database.Find.find(Player.class, new String[]{"puuid"}, new Object[]{puuid}, "fromPuuid");
  }

  @Nullable
  public static Player getPlayerFromPuuid(String puuid) {
    Player player = findPlayer(puuid);
    if (player == null) {
      final Summoner summoner = Xayah.summonerWithPuuid(puuid).get();
      if (summoner != null) {
        player = new Player(summoner.getName(), puuid);
        Database.save(player);
      }
    }
    return player;
  }

  @Nullable
  public static Player getPlayerFromName(String summonerName) {
    Player player = lookForPlayer(summonerName);
    if (player == null) {
      final Summoner summoner = Xayah.summonerNamed(summonerName).get();
      if (summoner == null) {
        log.fine("Der Spieler existiert nicht");
        return null;
      }
      player = new Player(summoner.getName(), summoner.getPuuid());
      Database.save(player);
    }
    return player;
  }

  private static Player lookForPlayer(String summonerName) {
    final Player player = determineExistingPlayerFromName(summonerName);
    if (player != null) {
      return player;
    }
    final Summoner summoner = Xayah.summonerNamed(summonerName).get();
    return summoner == null ? null : findPlayer(summoner.getPuuid());
  }

  private static Player determineExistingPlayerFromName(String summonerName) {
    return summonerName == null ? null : Database.Find.find(Player.class, new String[]{"name"}, new Object[]{summonerName}, "fromName");
  }
}
