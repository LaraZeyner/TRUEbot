package de.zahrie.trues.api.coverage.player;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.util.logger.Logger;
import org.jetbrains.annotations.Nullable;

public final class PlayerFactory {
  @Nullable
  public static Player getPlayer(String summonerName) {
    Player player = lookForPlayer(summonerName);
    if (player == null) {
      final Summoner summoner = Xayah.summonerNamed(summonerName).get();
      if (summoner == null) {
        Logger.getLogger("Player").attention("Der Spieler existiert nicht");
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
    return summoner == null ? null : determineExistingPlayerFromPuuid(summoner.getPuuid());
  }

  private static Player determineExistingPlayerFromName(String summonerName) {
    return summonerName == null ? null : Database.Find.find(Player.class, new String[]{"name"}, new Object[]{summonerName}, "fromName");
  }

  private static Player determineExistingPlayerFromPuuid(String puuid) {
    return puuid == null ? null : Database.Find.find(Player.class, new String[]{"puuid"}, new Object[]{puuid}, "fromPuuid");
  }
}
