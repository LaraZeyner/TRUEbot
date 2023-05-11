package de.zahrie.trues.api.coverage.player;

import java.util.List;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.PlayerImpl;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.Xayah;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
public final class PlayerFactory {
  @NonNull
  public static List<Player> registeredPlayers() {
    return new Query<>(Player.class).where(Condition.notNull("discord_user")).entityList();
  }

  @Nullable
  public static Player findPlayer(@Nullable String puuid) {
    return puuid == null ? null : new Query<>(Player.class).where("lol_puuid", puuid).entity();
  }

  @Nullable
  public static Player getPlayerFromPuuid(String puuid) {
    Player player = findPlayer(puuid);
    if (player == null) {
      final Summoner summoner = Xayah.summonerWithPuuid(puuid).get();
      if (summoner != null) player = new PlayerImpl(summoner.getName(), puuid).create();
    }
    return player;
  }

  @Nullable
  public static Player getPlayerFromName(String summonerName) {
    final Player player = lookForPlayer(summonerName);
    if (player != null) return player;

    final Summoner summoner = Xayah.summonerNamed(summonerName).get();
    if (summoner == null) {
      log.fine("Der Spieler existiert nicht");
      return null;
    }

    return new PlayerImpl(summoner.getName(), summoner.getPuuid()).create();
  }

  private static Player lookForPlayer(String summonerName) {
    final Player player = determineExistingPlayerFromName(summonerName);
    if (player != null) return player;

    final Summoner summoner = Xayah.summonerNamed(summonerName).get();
    return summoner == null ? null : findPlayer(summoner.getPuuid());
  }

  @Nullable
  private static Player determineExistingPlayerFromName(@Nullable String summonerName) {
    return summonerName == null ? null : new Query<>(Player.class).where("lol_name", summonerName).entity();
  }
}
