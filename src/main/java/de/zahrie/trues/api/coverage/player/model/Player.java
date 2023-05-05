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
@Table(value = "player", department = "league")
public class Player extends PlayerBase implements Entity<Player> {
  @Serial
  private static final long serialVersionUID = 2925841006082764104L;

  public Player(String summonerName, String puuid) {
    super(summonerName, puuid);
  }

  private Player(int id, String puuid, String summonerName, DiscordUser discordUser, Team team, LocalDateTime updated, boolean played) {
    super(id, puuid, summonerName, discordUser, team, updated, played);
  }

  public static Player get(Object[] objects) {
    return new Player(
        (int) objects[0],
        (String) objects[2],
        (String) objects[3],
        new Query<DiscordUser>().entity(objects[4]),
        new Query<Team>().entity(objects[5]),
        (LocalDateTime) objects[6],
        (boolean) objects[7]);
  }

  @Override
  public Player create() {
    return new Query<Player>().key("department", "league").key("lol_puuid", puuid)
        .col("lol_name", summonerName).col("discord_user", discordUser).col("team", team).col("updated", updated).col("played", played)
        .insert(this);
  }

}
