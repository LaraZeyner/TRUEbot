package de.zahrie.trues.api.coverage.player.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.performance.PerformanceFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(value = "player", department = "league")
public abstract class PlayerBase implements Comparable<PlayerBase>, Id, APlayer {
  protected int id; // player_id
  protected String puuid; // lol_puuid
  protected String summonerName; // lol_name
  protected DiscordUser discordUser; // discord_user
  protected TeamBase team; // team
  protected LocalDateTime updated; // updated
  protected boolean played = false; // played

  public PlayerBase(String summonerName, String puuid) {
    this.summonerName = summonerName;
    this.puuid = puuid;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setTeam(TeamBase team) {
    this.team = team;
    new Query<PlayerBase>().col("team", team).update(id);
  }

  public void setPuuidAndName(String puuid, String name) {
    this.puuid = puuid;
    this.summonerName = name;
    new Query<PlayerBase>().col("lol_puuid", puuid).col("lol_name", name).update(id);
  }

  public void setSummonerName(String summonerName) {
    this.summonerName = summonerName;
    new Query<PlayerBase>().col("lol_name", summonerName).update(id);
  }

  public void setDiscordUser(DiscordUser discordUser) {
    this.discordUser = discordUser;
    new Query<PlayerBase>().col("discord_user", discordUser).update(id);
  }

  public void setUpdated(LocalDateTime updated) {
    this.updated = updated;
    new Query<PlayerBase>().col("updated", updated).update(id);
  }

  public List<Rank> getRanks() {
    return new Query<Rank>().where("player", this).entityList();
  }

  public Rank getRankInSeason() {
    final PRMSeason lastSeason = SeasonFactory.getLastPRMSeason();
    if (lastSeason == null) throw new NoSuchElementException("Die letzte Season wurde nicht gefunden.");
    return getRankInSeason(lastSeason);
  }

  public Rank getRankInSeason(Season season) {
    return new Query<Rank>().where("player", this).and("season", season).entity();
  }

  public Rank getLastRank() {
    return getLastRank(Tier.UNRANKED, Division.IV);
  }

  public Rank getLastRank(Tier tier, Division division) {
    return getRanks().stream().max(Comparator.naturalOrder()).orElse(new Rank(this, tier, division, (byte) 0, 0, 0));
  }

  public Rank getLastRelevantRank() {
    return getRanks().stream().sorted(Comparator.reverseOrder())
        .filter(rank -> rank.getWinrate().getGames() >= 50)
        .findFirst().orElse(getLastRank(Tier.SILVER, Division.I));
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof PlayerBase && this.id == ((PlayerBase) obj).id;
  }


  @Override
  public int compareTo(@NotNull PlayerBase o) {
    return getLastRank().compareTo(o.getLastRank());
  }

  @Override
  public String toString() {
    return summonerName + " | " + getLastRank();
  }

  public List<Object[]> getLastGames(GameType gameType) {
    return PerformanceFactory.getLastPlayerGames(gameType, this);
  }
}
