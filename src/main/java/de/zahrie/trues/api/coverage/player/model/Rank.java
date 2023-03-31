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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
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
@NamedQuery(name = "Rank.fromPlayer", query = "FROM Rank WHERE player = :player ORDER BY season desc")
@NamedQuery(name = "Rank.fromPlayerOnSeason", query = "FROM Rank WHERE player = :player AND season = :season")
public class Rank implements Serializable {
  @Serial
  private static final long serialVersionUID = 7228597517124074472L;

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

  @Column(name = "tier", length = 11, nullable = false)
  @Enumerated(EnumType.STRING)
  private Tier tier;

  @Column(name = "division", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private Division division;

  @Column(name = "points", nullable = false)
  private byte points;

  @Column(name = "wins", columnDefinition = "SMALLINT UNSIGNED")
  private int wins;

  @Column(name = "losses", columnDefinition = "SMALLINT UNSIGNED")
  private int losses;

  @Column(name = "mmr", columnDefinition = "SMALLINT UNSIGNED")
  private int mmr;

  public Standing getWinrate() {
    return new Standing(wins, losses);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Rank && this.player.equals(((Rank) obj).getPlayer());
  }

  @Override
  public String toString() {
    return tier + " " + division + " - " + points + " LP (" + getWinrate().format(Format.ADDITIONAL) + ")";
  }

  Rank(Player player, Tier tier, Division division, byte points, int wins, int losses) {
    this(SeasonFactory.getLastSeason(), player, tier, division, points, wins, losses);
  }

  Rank(Season season, Player player, Tier tier, Division division, byte points, int wins, int losses) {
    this.season = season;
    this.player = player;
    this.tier = tier;
    this.division = division;
    this.points = points;
    this.wins = wins;
    this.losses = losses;
  }

}
