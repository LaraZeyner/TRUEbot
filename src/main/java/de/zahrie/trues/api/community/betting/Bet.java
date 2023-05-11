package de.zahrie.trues.api.community.betting;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Table("bet")
public final class Bet implements Entity<Bet> {
  @Serial
  private static final long serialVersionUID = -3029760281191504565L;

  private int id;
  private final Match match; // coverage
  private final DiscordUser user; // discord_user
  private final String outcome; // bet_outcome
  private final int amount; // bet_amount

  public static Bet get(List<Object> objects) {
    return new Bet(
        (int) objects.get(0),
        new Query<>(Match.class).entity(objects.get(1)),
        new Query<>(DiscordUser.class).entity(objects.get(2)),
        (String) objects.get(3),
        (int) objects.get(4)
    );
  }

  @Override
  public Bet create() {
    return new Query<>(Bet.class)
        .key("coverage", match).key("discord_user", user)
        .col("bet_outcome", outcome).col("bet_amount", amount)
        .insert(this);
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }
}
