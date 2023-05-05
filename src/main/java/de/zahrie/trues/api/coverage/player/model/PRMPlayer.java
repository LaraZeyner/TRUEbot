package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "player", department = "prime")
public class PRMPlayer extends PlayerBase implements Entity<PRMPlayer> {
  @Serial
  private static final long serialVersionUID = 1620593763353601777L;

  private Integer prmUserId; // prm_id

  public PRMPlayer(String summonerName, String puuid, Integer prmUserId) {
    super(summonerName, puuid);
    this.prmUserId = prmUserId;
  }

  private PRMPlayer(int id, String puuid, String summonerName, DiscordUser discordUser, Team team, LocalDateTime updated, boolean played, Integer prmUserId) {
    super(id, puuid, summonerName, discordUser, team, updated, played);
    this.prmUserId = prmUserId;
  }

  public static PRMPlayer get(Object[] objects) {
    return new PRMPlayer(
        (int) objects[0],
        (String) objects[2],
        (String) objects[3],
        new Query<DiscordUser>().entity(objects[4]),
        new Query<Team>().entity(objects[5]),
        (LocalDateTime) objects[6],
        (boolean) objects[7],
        (Integer) objects[8]);
  }

  @Override
  public PRMPlayer create() {
    return new Query<PRMPlayer>().key("lol_puuid", puuid)
        .col("department", "prime").col("lol_name", summonerName).col("discord_user", discordUser).col("team", team)
        .col("updated", updated).col("played", played).col("prm_id", prmUserId)
        .insert(this);
  }

  public void setPrmUserId(Integer prmUserId) {
    this.prmUserId = prmUserId;
    new Query<PRMPlayer>().col("department", prmUserId == null ? "league" : "prime").col("prm_id", prmUserId).update(id);
  }

  @Override
  public boolean equals(Object other) {
    return super.equals(other) || (other instanceof Player && this.getId() == ((Player) other).getId());
  }
}
