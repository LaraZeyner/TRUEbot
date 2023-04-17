package de.zahrie.trues.api.coverage.player;

import java.util.List;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.riot.Xayah;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.util.Util;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
public final class PlayerFactory {
  public static List<Player> registeredPlayers() {
    return QueryBuilder.hql(Player.class, "FROM Player WHERE discordUser is not null").list();
  }
  public static Player findPlayer(String puuid) {
    return Util.avoidNull(puuid, null,
        puuidStr -> QueryBuilder.hql(Player.class, "FROM Player WHERE puuid = " + puuidStr).single());
  }

  @Nullable
  public static Player getPlayerFromPuuid(String puuid) {
    Player player = findPlayer(puuid);
    if (player == null) {
      final Summoner summoner = Xayah.summonerWithPuuid(puuid).get();
      if (summoner != null) {
        player = new Player(summoner.getName(), puuid);
        Database.insert(player);
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
      Database.insert(player);
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
    return Util.avoidNull(summonerName, null,
        nameStr -> QueryBuilder.hql(Player.class, "FROM Player WHERE summonerName = " + nameStr).single());
  }
}
