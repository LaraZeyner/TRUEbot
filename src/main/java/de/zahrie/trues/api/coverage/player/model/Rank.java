package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.io.Serializable;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.util.Format;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "player_ranked", indexes = @Index(name = "idx_lplayer", columnList = "player"))
public class Rank extends AbstractRank implements Serializable {
  @Serial
  private static final long serialVersionUID = -3473800152212072760L;

  public static AbstractRank fromMMR(int mmr) {
    return new AbstractRank(mmr);
  }


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "rank_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "player_player_id", nullable = false)
  private Player player;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "season", nullable = false)
  private Season season;

  @Column(name = "wins", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int wins;

  @Column(name = "losses", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int losses;

  public Standing getWinrate() {
    return new Standing(wins, losses);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Rank && this.player.equals(((Rank) obj).getPlayer());
  }

  @Override
  public String toString() {
    return super.toString() + (getTier().equals(RankTier.UNRANKED) ? "" : " (" + getWinrate().format(Format.ADDITIONAL) + ")");
  }

  Rank(Player player, Tier tier, Division division, byte points, int wins, int losses) {
    this(SeasonFactory.getLastPRMSeason(), player, tier, division, points, wins, losses);
  }

  Rank(Season season, Player player, Tier tier, Division division, byte points, int wins, int losses) {
    super(tier, division, points);
    this.season = season;
    this.player = player;
    this.wins = wins;
    this.losses = losses;
  }
}
