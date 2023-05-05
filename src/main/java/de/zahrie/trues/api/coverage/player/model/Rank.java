package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;

import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.util.Format;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("player_ranked")
public class Rank implements Entity<Rank>, Comparable<Rank> {
  @Serial
  private static final long serialVersionUID = 4008920298892200060L;

  public static AbstractRank fromMMR(int mmr) {
    return AbstractRank.fromMMR(mmr);
  }

  private int id; // player_ranked_id
  private Season season; // season
  private PlayerBase player; // player
  private AbstractRank rank;
  private int wins; // wins
  private int losses; // losses

  public Standing getWinrate() {
    return new Standing(wins, losses);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Rank && this.player.equals(((Rank) obj).getPlayer());
  }

  @Override
  public String toString() {
    return super.toString() + (rank.tier().equals(AbstractRank.RankTier.UNRANKED) ? "" : " (" + getWinrate().format(Format.ADDITIONAL) + ")");
  }

  public Rank(PlayerBase player, Tier tier, Division division, byte points, int wins, int losses) {
    this.season = SeasonFactory.getLastPRMSeason();
    this.player = player;
    this.rank = new AbstractRank(tier, division, points);
    this.wins = wins;
    this.losses = losses;
  }

  public static Rank get(Object[] objects) {
    return new Rank(
        (int) objects[0],
        new Query<Season>().entity(objects[1]),
        new Query<Player>().entity(objects[2]),
        new AbstractRank(Tier.valueOf(new SQLEnum<AbstractRank.RankTier>().of(objects[3]).name()), new SQLEnum<Division>().of(objects[4]), (byte) objects[5]),
        (int) objects[6],
        (int) objects[7]
    );
  }

  @Override
  public Rank create() {
    return new Query<Rank>().key("season", season).key("player", player)
        .col("tier", rank.tier()).col("division", rank.division()).col("points", rank.points())
        .col("wins", wins).col("losses", losses).insert(this);
  }

  @Override
  public int compareTo(@NotNull Rank o) {
    return rank.compareTo(o.getRank());
  }
}
