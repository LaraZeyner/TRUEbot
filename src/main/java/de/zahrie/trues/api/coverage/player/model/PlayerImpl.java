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
@Table(value = "player", department = "other")
public class PlayerImpl extends Player implements Entity<PlayerImpl> {
  @Serial
  private static final long serialVersionUID = 2925841006082764104L;

  public PlayerImpl(String summonerName, String puuid) {
    super(summonerName, puuid);
  }

  private PlayerImpl(int id, String puuid, String summonerName, DiscordUser discordUser, Team team, LocalDateTime updated, boolean played) {
    super(id, puuid, summonerName, discordUser, team, updated, played);
  }

  public static PlayerImpl get(List<Object> objects) {
    return new PlayerImpl(
        (int) objects.get(0),
        (String) objects.get(2),
        (String) objects.get(3),
        new Query<>(DiscordUser.class).entity(objects.get(4)),
        new Query<>(Team.class).entity(objects.get(5)),
        (LocalDateTime) objects.get(6),
        (boolean) objects.get(7));
  }

  @Override
  public PlayerImpl create() {
    return new Query<>(PlayerImpl.class).key("lol_puuid", puuid)
        .col("lol_name", summonerName).col("discord_user", discordUser).col("team", team).col("updated", updated).col("played", played)
        .insert(this);
  }

}
