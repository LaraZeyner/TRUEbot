package de.zahrie.trues.api.coverage.player;

import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.PlayerImpl;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.Zeri;
import de.zahrie.trues.util.io.log.Console;
import de.zahrie.trues.util.io.log.DevInfo;
import lombok.NonNull;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.jetbrains.annotations.Nullable;

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
      final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByPUUID(LeagueShard.EUW1, puuid);
      if (summoner != null) player = new PlayerImpl(summoner.getName(), puuid).create();
    }
    return player;
  }

  @Nullable
  public static Player getPlayerFromName(String summonerName) {
    final Player player = lookForPlayer(summonerName);
    if (player != null) return player;

    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, summonerName);
    if (summoner == null) {
      new DevInfo("Der Spieler **" + summonerName + "** existiert nicht").with(Console.class).warn();
      return null;
    }

    return new PlayerImpl(summoner.getName(), summoner.getPUUID()).create();
  }

  private static Player lookForPlayer(String summonerName) {
    final Player player = determineExistingPlayerFromName(summonerName);
    if (player != null) return player;

    final Summoner summoner = Zeri.get().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, summonerName);
    return summoner == null ? null : findPlayer(summoner.getPUUID());
  }

  @Nullable
  private static Player determineExistingPlayerFromName(@Nullable String summonerName) {
    return summonerName == null ? null : new Query<>(Player.class).where("lol_name", summonerName).entity();
  }
}
