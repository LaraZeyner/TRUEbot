package de.zahrie.trues.api.coverage.player.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.riot.performance.PerformanceFactory;
import de.zahrie.trues.api.scouting.PlayerAnalyzer;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.io.log.DevInfo;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@Table(value = "player")
public abstract class Player implements Comparable<Player>, Id, APlayer {
  protected int id; // player_id
  protected String puuid; // lol_puuid
  protected String summonerName; // lol_name
  protected DiscordUser discordUser; // discord_user
  protected Team team; // team
  protected LocalDateTime updated; // updated
  protected final boolean played; // played

  public Player(String summonerName, String puuid) {
    this.summonerName = summonerName;
    this.puuid = puuid;
    this.played = false;
  }

  protected Player(int id, String puuid, String summonerName, DiscordUser discordUser, Team team, LocalDateTime updated, boolean played) {
    this.id = id;
    this.puuid = puuid;
    this.summonerName = summonerName;
    this.discordUser = discordUser;
    this.team = team;
    this.updated = updated;
    this.played = played;
  }

  public void setId(int id) {
    this.id = id;
  }


  public void setTeam(Team team) {
    this.team = team;
    team.getPlayers().add(this);
    new Query<>(Player.class).col("team", team.getId()).update(id);
  }

  public void setDiscordUser(DiscordUser discordUser) {
    this.discordUser = discordUser;
    new Query<>(DiscordUser.class).col("discord_user", discordUser).update(id);
  }

  public void setPuuidAndName(String puuid, String name) {
    this.puuid = puuid;
    this.summonerName = name;
    new Query<>(Player.class).col("lol_puuid", puuid).col("lol_name", name).update(id);
  }

  public void setSummonerName(String summonerName) {
    this.summonerName = summonerName;
    new Query<>(Player.class).col("lol_name", summonerName).update(id);
  }

  public void setUpdated(LocalDateTime updated) {
    this.updated = updated;
    new Query<>(Player.class).col("updated", updated).update(id);
  }

  public List<PlayerRank> getRanks() {
    return new Query<>(PlayerRank.class).where("player", this).entityList();
  }

  public PlayerRank getRankInSeason() {
    final PRMSeason lastSeason = SeasonFactory.getLastPRMSeason();
    if (lastSeason == null) {
      new DevInfo().warn(new NoSuchElementException("Die letzte Season wurde nicht gefunden."));
      return null;
    }
    return getRankInSeason(lastSeason);
  }

  public PlayerRank getRankInSeason(Season season) {
    return new Query<>(PlayerRank.class).where("player", this).and("season", season).entity();
  }

  public PlayerRank getLastRank() {
    return getLastRank(Tier.UNRANKED, Division.IV);
  }

  public PlayerRank getLastRank(Tier tier, Division division) {
    return getRanks().stream().max(Comparator.naturalOrder()).orElse(new PlayerRank(this, tier, division, (byte) 0, 0, 0));
  }

  public PlayerRank getLastRelevantRank() {
    return getRanks().stream().sorted(Comparator.reverseOrder())
        .filter(rank -> rank.getWinrate().getGames() >= 50)
        .findFirst().orElse(getLastRank(Tier.SILVER, Division.I));
  }

  public PlayerAnalyzer analyze(ScoutingGameType type, int days) {
    return new PlayerAnalyzer(this, type, days);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Player && this.id == ((Player) obj).id;
  }


  @Override
  public int compareTo(@NotNull Player o) {
    return Integer.compare(getId(), o.getId());
  }

  @Override
  public String toString() {
    return summonerName + " | " + getLastRank();
  }

  public List<Object[]> getLastGames(GameType gameType) {
    return PerformanceFactory.getLastPlayerGames(gameType, this);
  }
}
