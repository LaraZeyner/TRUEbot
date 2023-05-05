package de.zahrie.trues.api.riot.matchhistory.performance;

import java.io.Serial;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("performance")
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

  public static Performance get(Object[] objects) {
    return new Performance(
        (int) objects[0],
        new Query<TeamPerf>().entity( objects[1]),
        new Query<Player>().entity( objects[2]),
        new SQLEnum<Lane>().of(objects[3]),
        new Matchup(new Query<Champion>().entity( objects[4]), new Query<Champion>().entity( objects[5])),
        new KDA((short) objects[6], (short) objects[7], (short) objects[8]),
        (int) objects[9],
        (Integer) objects[10],
        (Integer) objects[11],
        (int) objects[12]);
  }

  @Override
  public Performance create() {
    return new Query<Performance>()
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
