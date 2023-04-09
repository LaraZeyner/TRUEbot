package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.PerformanceFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorFormula;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "player", indexes = {
    @Index(name = "idx_lol_name", columnList = "lol_name"),
    @Index(name = "idx_lol_puuid", columnList = "lol_puuid", unique = true)
})
@DiscriminatorFormula("IF(prm_id IS NULL, 'null', 'not null')")
@NamedQuery(name = "Player.getWinsMatchmade", query = "SELECT champion, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND teamPerformance.win = true GROUP BY champion ORDER BY count(p) desc")
public class Player implements Serializable {

  public Player(String summonerName, String puuid) {
    this.summonerName = summonerName;
    this.puuid = puuid;
  }

  @Serial
  private static final long serialVersionUID = 4484554092865667168L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "player_id", nullable = false)
  private int id;

  @Column(name = "lol_puuid", nullable = false, length = 78, unique = true)
  private String puuid;

  @Column(name = "lol_name", nullable = false, length = 16, unique = true)
  private String summonerName;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "discord_user", unique = true)
  @ToString.Exclude
  private DiscordUser discordUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  @ToString.Exclude
  private Team team;

  @Column(name = "updated", nullable = false)
  private LocalDateTime updated;

  @Column(name = "played", nullable = false)
  private boolean played = false;

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private Set<Rank> ranks;

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private Set<Performance> performances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private Set<Lineup> lineups = new LinkedHashSet<>();

  public Rank getLastRank() {
    return getLastRank(Tier.UNRANKED, Division.IV);
  }

  public Rank getLastRank(Tier tier, Division division) {
    return ranks.stream().max(Comparator.naturalOrder())
        .orElse(new Rank(this, tier, division, (byte) 0, 0, 0));
  }

  public Rank getLastRelevantRank() {
    return ranks.stream().sorted(Comparator.reverseOrder())
        .filter(rank -> rank.getWinrate().getGames() >= 50)
        .findFirst().orElse(getLastRank(Tier.SILVER, Division.I));
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Player && this.id == ((Player) obj).id;
  }

  public void setDiscordUser(DiscordUser discordUser) {
    if (this.discordUser != null && !this.discordUser.equals(discordUser)) {
      this.discordUser.setPlayer(null);
      Database.save(this.discordUser);
    }
    this.discordUser = discordUser;
    Database.save(this);
    Database.save(discordUser);
  }

  @Override
  public String toString() {
    return summonerName + " | " + getLastRank();
  }

  public List<Object[]> getLastGames(GameType gameType) {
    return PerformanceFactory.getLastPlayerGames(gameType, this);
  }
}
