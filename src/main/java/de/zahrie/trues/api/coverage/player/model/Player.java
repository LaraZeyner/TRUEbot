package de.zahrie.trues.api.coverage.player.model;

import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.riot.performance.PerformanceFactory;
import de.zahrie.trues.api.scouting.PlayerAnalyzer;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.api.scouting.analyze.RiotPlayerAnalyzer;
import de.zahrie.trues.util.Util;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@Table(value = "player")
public abstract class Player implements Comparable<Player>, Id, APlayer {
  protected int id; // player_id
  protected String puuid; // lol_puuid
  protected String summonerName; // lol_name
  protected Integer discordUserId; // discord_user
  protected Integer teamId; // team
  protected LocalDateTime updated; // updated
  protected boolean played; // played

  protected DiscordUser discordUser;

  public DiscordUser getDiscordUser() {
    if (discordUser == null) this.discordUser = new Query<>(DiscordUser.class).entity(discordUserId);
    return discordUser;
  }

  public void setDiscordUser(DiscordUser discordUser) {
    if (this.discordUser.equals(discordUser)) return;
    this.discordUser = discordUser;
    this.discordUserId = Util.avoidNull(discordUser, DiscordUser::getId);
    new Query<>(Player.class).col("discord_user", discordUserId).update(id);
    loadGames(LoaderGameType.CLASH_PLUS);
  }

  public void setPlayed(boolean played) {
    if (this.played != played) new Query<>(Player.class).col("played", played).update(id);
    this.played = played;
  }

  protected Team team;

  public Team getTeam() {
    if (team == null) this.team = new Query<>(Team.class).entity(teamId);
    return team;
  }

  public void setTeam(Team team) {
    if (getTeam() == team) return;
    if (teamId != null && teamId == team.getId()) return;

    this.team = team;
    this.teamId = Util.avoidNull(team, Team::getId);
    if (team != null) {
      team.getPlayers().add(this);
      if (team instanceof PRMTeam prmTeam && prmTeam.getCurrentLeague() != null && prmTeam.getCurrentLeague().getLeague().isOrgaLeague()) {
        System.out.println("NEUER SPIELER");
        loadGames(LoaderGameType.CLASH_PLUS);
      }
    }
    new Query<>(Player.class).col("team", team).update(id);

  }

  public Player(String summonerName, String puuid) {
    this.summonerName = summonerName;
    this.puuid = puuid;
    final Player playerFound = new Query<>(Player.class).where("lol_puuid", puuid).entity();
    if (playerFound != null) {
      this.updated = playerFound.getUpdated();
      this.played = playerFound.isPlayed();
      this.discordUserId = playerFound.getDiscordUserId();
      this.teamId = playerFound.getTeamId();
    } else {
      this.updated = LocalDateTime.now().minusYears(1);
      this.played = false;
    }
  }

  protected Player(int id, String puuid, String summonerName, Integer discordUserId, Integer teamId, LocalDateTime updated, boolean played) {
    this.id = id;
    this.puuid = puuid;
    this.summonerName = summonerName;
    this.discordUserId = discordUserId;
    this.teamId = teamId;
    this.updated = updated;
    this.played = played;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setPuuidAndName(String puuid, String name) {
    this.puuid = puuid;
    this.summonerName = name;
    new Query<>(Player.class).col("lol_puuid", puuid).col("lol_name", name).update(id);
  }

  public void setSummonerName(String summonerName) {
    if (!summonerName.equals(this.summonerName)) new Query<>(Player.class).col("lol_name", summonerName).update(id);
    this.summonerName = summonerName;
  }

  public void setUpdated(LocalDateTime updated) {
    this.updated = updated;
    new Query<>(Player.class).col("updated", updated).update(id);
  }

  private PlayerRankHandler ranks;

  public PlayerRankHandler getRanks() {
    if (ranks == null) this.ranks = new PlayerRankHandler(this);
    return ranks;
  }

  private PlayerAnalyzer analyzer;

  public PlayerAnalyzer analyze(ScoutingGameType type, int days) {
    if (type.equals(ScoutingGameType.MATCHMADE) && days == 180) {
      if (analyzer == null) analyzer = new PlayerAnalyzer(this, type, days);
      return analyzer;
    }
    return new PlayerAnalyzer(this, type, days);
  }

  public void loadGames(LoaderGameType gameType) {
    new RiotPlayerAnalyzer(this).analyze(gameType, false);
  }

  public void forceLoadMatchmade() {
    new RiotPlayerAnalyzer(this).analyze(LoaderGameType.MATCHMADE, true);
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
    return summonerName + " | " + getRanks().getCurrent();
  }

  public List<Object[]> getLastGames(GameType gameType) {
    return PerformanceFactory.getLastPlayerGames(gameType, this);
  }
}
