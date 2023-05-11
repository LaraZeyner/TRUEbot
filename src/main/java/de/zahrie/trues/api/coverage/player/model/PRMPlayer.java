package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

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
public class PRMPlayer extends Player implements Entity<PRMPlayer> {
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

  public static PRMPlayer get(List<Object> objects) {
    return new PRMPlayer(
        (int) objects.get(0),
        (String) objects.get(2),
        (String) objects.get(3),
        new Query<>(DiscordUser.class).entity(objects.get(4)),
        new Query<>(Team.class).entity(objects.get(5)),
        (LocalDateTime) objects.get(6),
        (boolean) objects.get(7),
        (Integer) objects.get(8));
  }

  @Override
  public PRMPlayer create() {
    return new Query<>(PRMPlayer.class).key("lol_puuid", puuid)
        .col("lol_name", summonerName).col("discord_user", discordUser).col("team", team).col("updated", updated).col("played", played)
        .col("prm_id", prmUserId)
        .insert(this);
  }

  public void setPrmUserId(Integer prmUserId) {
    this.prmUserId = prmUserId;
    new Query<>(PRMPlayer.class).col("department", prmUserId == null ? "league" : "prime").col("prm_id", prmUserId).update(id);
  }

  @Override
  public boolean equals(Object other) {
    return super.equals(other) || (other instanceof Player && this.getId() == ((Player) other).getId());
  }
}
