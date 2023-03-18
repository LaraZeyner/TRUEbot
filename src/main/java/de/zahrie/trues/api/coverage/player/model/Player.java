package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.PerformanceFactory;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.util.Const;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
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
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "player", indexes = { @Index(name = "idx_lol_name", columnList = "lol_name"),
        @Index(name = "prm_id", columnList = "prm_id", unique = true),
        @Index(name = "lol_puuid", columnList = "lol_puuid", unique = true),
        @Index(name = "team", columnList = "team"),
        @Index(name = "discord_name", columnList = "discord_user", unique = true) })
@DiscriminatorFormula("IF(prm_id IS NULL, 'null', 'not null')")
@NamedNativeQuery(name = "Player.getLanePlayed", query = "SELECT lane, COUNT(*) FROM performance WHERE (t_perf IN (SELECT t_perf FROM performance JOIN player ON player=player_id JOIN team_perf ON t_perf=t_perf_id JOIN game ON game=game_id WHERE team IS NOT NULL AND team=%s AND game_type = 'ranked' AND start_time > %s GROUP BY t_perf, player.team HAVING COUNT(*) > 2 ORDER BY COUNT(*) DESC) OR t_perf IN (SELECT t_perf FROM performance JOIN team_perf ON t_perf_id=t_perf JOIN game ON game_id=game WHERE player=%s AND game_type IN ('tourney', 'clash') AND start_time > %s GROUP BY t_perf ORDER BY COUNT(*) DESC)) AND player=%s GROUP BY lane ORDER BY COUNT(*) DESC")
@NamedQuery(name = "Player.fromName", query = "FROM PrimePlayer WHERE summonerName = :name")
@NamedQuery(name = "Player.fromPuuid", query = "FROM PrimePlayer WHERE puuid = :puuid")
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

  @Column(name = "lol_puuid", nullable = false, length = 78)
  private String puuid;

  @Column(name = "lol_name", nullable = false, length = 16)
  private String summonerName;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "discord_user")
  @ToString.Exclude
  private DiscordMember member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  @ToString.Exclude
  private Team team;

  @OneToOne(mappedBy = "player")
  @ToString.Exclude
  private Rank rank;

  @Column(name = "elo", length = 21)
  private String elo = "Unranked";

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "updated", nullable = false)
  private Time updated;

  @Column(name = "played", nullable = false)
  private boolean played = false;

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private Set<Performance> performances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private Set<Lineup> lineups = new LinkedHashSet<>();

  public boolean isType(PlayerType type) {
    // TODO (Abgie) 15.03.2023: never used
    if (type == null) {
      return true;
    }

    return switch (type) {
      case PRIME_PLAYER -> this instanceof PrimePlayer;
      case RANKED_PLAYER -> this.rank != null;
      case REGISTERED_PLAYER -> this.member != null;
      case TEAM_PLAYER -> this instanceof PrimePlayer && this.getTeam() != null;
    };

  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Player && this.id == ((Player) obj).id;
  }

  public int getMMR() {
    // TODO (Abgie) 15.03.2023: never used
    return this.rank == null ? Const.PLAYER_MMR_DEFAULT_VALUE : this.rank.getMmr();
  }

  public void setMember(DiscordMember member) {
    if (this.member != null && !this.member.equals(member)) {
      this.member.setPlayer(null);
      Database.save(this.member);
    }
    this.member = member;
    Database.save(this);
    Database.save(member);
  }

  @Override
  public String toString() {
    return summonerName + " | " + getRank();
  }

  public List<Object[]> getLastGames(GameType gameType) {
    return PerformanceFactory.getLastPlayerGames(gameType, this);
  }
}
