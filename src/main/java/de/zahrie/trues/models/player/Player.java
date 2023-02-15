package de.zahrie.trues.models.player;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.models.coverage.Lineup;
import de.zahrie.trues.models.discord.member.DiscordMember;
import de.zahrie.trues.models.riot.matchhistory.Performance;
import de.zahrie.trues.models.riot.rank.LeaguePoints;
import de.zahrie.trues.models.riot.rank.Rank;
import de.zahrie.trues.models.riot.rank.RankDivision;
import de.zahrie.trues.models.riot.rank.RankTier;
import de.zahrie.trues.models.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "player", indexes = { @Index(name = "idx_lol_name", columnList = "lol_name"),
        @Index(name = "prm_id", columnList = "prm_id", unique = true),
        @Index(name = "lol_puuid", columnList = "lol_puuid", unique = true),
        @Index(name = "team", columnList = "team"),
        @Index(name = "discord_name", columnList = "discord_user", unique = true) })
public class Player implements Serializable {
  @Serial
  private static final long serialVersionUID = 4484554092865667168L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "player_id", nullable = false)
  private int id;

  @Column(name = "prm_id")
  private Integer prmUserId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  @ToString.Exclude
  private Team team;

  @Column(name = "lol_puuid", nullable = false, length = 78)
  private String puuid;

  @Column(name = "lol_name", nullable = false, length = 16)
  private String summonerName;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "discord_user")
  @ToString.Exclude
  private DiscordMember member;

  @Column(name = "mmr", nullable = false)
  private short mmr = 1300;

  @Column(name = "elo", length = 21)
  private String elo = "Unranked";

  @Column(name = "wins", columnDefinition = "SMALLINT UNSIGNED")
  private Integer wins;

  @Column(name = "losses", columnDefinition = "SMALLINT UNSIGNED")
  private Integer losses;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated", nullable = false)
  private Calendar updated;

  @Column(name = "played", nullable = false)
  private boolean played = false;

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private Set<Performance> performances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private Set<Lineup> lineups = new LinkedHashSet<>();

  public Rank getRank() {
    if (this.elo.contains("Unranked")) {
      return new Rank();
    }
    final String tier = this.elo.split(" ")[0];
    final String division = this.elo.split(" ")[1];
    final int division_number = division.equals("IV") ? 4 : division.length();
    final RankDivision div = new RankDivision(RankTier.valueOf(tier), division_number);
    final String leaguePoints = this.elo.split(" ")[2];
    return new Rank(div, new LeaguePoints(Integer.parseInt(leaguePoints)));
  }

  public String getWinrate() {
    if (this.wins > 0) {
      return Math.round(this.wins * 100.0 / (this.wins + this.losses))  + "%";
    }
    return null;
  }

  public boolean isType(PlayerType type) {
    if (type == null) {
      return true;
    }

    return switch (type) {
      case PRIME_PLAYER -> this.prmUserId != null;
      case RANKED_PLAYER -> this.elo != null && !this.elo.contains("Unranked");
      case REGISTERED_PLAYER -> this.member != null;
      case TEAM_PLAYER -> this.team != null;
    };

  }

}