package de.zahrie.trues.api.riot.performance;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.KDA;
import de.zahrie.trues.api.riot.champion.Champion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("performance")
@ExtensionMethod(SQLUtils.class)
public class Performance implements Entity<Performance>, Comparable<Performance> {
  @Serial
  private static final long serialVersionUID = -8274031327889064909L;

  private int id; // perf_id
  private final TeamPerf teamPerformance; // t_perf
  private final Player player; // player
  private final Lane lane; // lane
  private final Matchup matchup; // champion, enemy_champion
  private final KDA kda;
  private final int gold; // gold
  private final Integer damage; // damage
  private final Integer vision; // vision
  private final int creeps; // creeps

  public static Performance get(List<Object> objects) {
    return new Performance(
        (int) objects.get(0),
        new Query<>(TeamPerf.class).entity(objects.get(1)),
        new Query<>(Player.class).entity(objects.get(2)),
        new SQLEnum<>(Lane.class).of(objects.get(3)),
        new Matchup(new Query<>(Champion.class).entity( objects.get(4)), new Query<>(Champion.class).entity(objects.get(5))),
        new KDA(objects.get(6).shortValue(), objects.get(7).shortValue(), objects.get(8).shortValue()),
        (int) objects.get(9),
        (Integer) objects.get(10),
        (Integer) objects.get(11),
        (int) objects.get(12));
  }

  @Override
  public Performance create() {
    return new Query<>(Performance.class)
        .key("t_perf", teamPerformance).key("player", player).key("lane", lane).key("champion", matchup.champion()).key("enemy_champion", matchup.opponent())
        .key("kills", kda.kills()).key("deaths", kda.deaths()).key("assists", kda.assists()).key("gold", gold).key("damage", gold)
        .key("vision", vision).key("creeps", creeps)
        .insert(this);
  }

  public String getPlayername() {
    return player.getSummonerName();
  }

  public String getStats() {
    return kda.toString() + "(" + (lane.equals(Lane.UTILITY) ? vision : creeps) + ")";
  }

  @Override
  public int compareTo(@NotNull Performance o) {
    return lane.compareTo(o.getLane());
  }
}
