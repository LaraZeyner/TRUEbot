package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.util.List;

import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.util.Format;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table("player_ranked")
@ExtensionMethod(SQLUtils.class)
public class PlayerRank implements Entity<PlayerRank>, Comparable<PlayerRank> {
  @Serial
  private static final long serialVersionUID = 4008920298892200060L;

  public static Rank fromMMR(int mmr) {
    return Rank.fromMMR(mmr);
  }

  private int id; // player_ranked_id
  private Season season; // season
  private Player player; // player
  private Rank rank;
  private int wins; // wins
  private int losses; // losses

  public Standing getWinrate() {
    return new Standing(wins, losses);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof PlayerRank && this.player.equals(((PlayerRank) obj).getPlayer());
  }

  @Override
  public String toString() {
    return rank.toString() + (rank.tier().equals(Rank.RankTier.UNRANKED) ? "" : " - (" + getWinrate().format(Format.ADDITIONAL) + ")");
  }

  public PlayerRank(Player player, Tier tier, Division division, byte points, int wins, int losses) {
    this.season = SeasonFactory.getLastPRMSeason();
    this.player = player;
    this.rank = new Rank(tier, division, points);
    this.wins = wins;
    this.losses = losses;
  }

  public static PlayerRank get(List<Object> objects) {
    return new PlayerRank(
        (int) objects.get(0),
        new Query<>(Season.class).entity(objects.get(1)),
        new Query<>(Player.class).entity(objects.get(2)),
        new Rank(Tier.valueOf(new SQLEnum<>(Rank.RankTier.class).of(objects.get(3)).name()), new SQLEnum<>(Division.class).of(objects.get(4)), objects.get(5).byteValue()),
        (int) objects.get(6),
        (int) objects.get(7)
    );
  }

  @Override
  public PlayerRank create() {
    return new Query<>(PlayerRank.class).key("season", season).key("player", player)
        .col("tier", rank.tier()).col("division", rank.division()).col("points", rank.points())
        .col("wins", wins).col("losses", losses).insert(this);
  }

  @Override
  public int compareTo(@NotNull PlayerRank o) {
    return rank.compareTo(o.getRank());
  }
}
