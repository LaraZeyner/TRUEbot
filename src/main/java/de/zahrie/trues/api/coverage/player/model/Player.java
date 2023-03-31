package de.zahrie.trues.api.coverage.player.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.PerformanceFactory;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.database.types.TimeCoverter;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.ExtensionMethod;
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
@NamedQuery(name = "Player.getGamesPRM", query = "SELECT count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney'")
@NamedQuery(name = "Player.getGamesPRMClash", query = "SELECT count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash')")
@NamedQuery(name = "Player.getGamesPRMClash", query = "SELECT count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash')")
@NamedQuery(name = "Player.getGamesTeamGames", query = "SELECT count(distinct teamPerformance) FROM Performance p WHERE (teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team is not null AND player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance, player.team HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player = :player AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC)) AND player = :player")
@NamedQuery(name = "Player.getGamesMatchmade", query = "SELECT count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start")
@NamedQuery(name = "Player.getWinsMatchmade", query = "SELECT champion, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND teamPerformance.win = true GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "Player.getPicksPRM", query = "SELECT champion, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney' GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "Player.getPicksPRMClash", query = "SELECT champion, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "Player.getPicksTeamGames", query = "SELECT champion, count(p) FROM Performance p WHERE (teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team is not null AND player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance, player.team HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player = :player AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC)) AND player = :player GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "Player.getPicksMatchmade", query = "SELECT champion, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start GROUP BY champion ORDER BY count(p) desc")
@NamedQuery(name = "Player.getMatchupsPRM", query = "SELECT opponent, count(p), avg(teamPerformance.win) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney' GROUP BY champion HAVING count(p) > 4 ORDER BY avg(teamPerformance.win)")
@NamedQuery(name = "Player.getMatchupsPRMClash", query = "SELECT opponent, count(p), avg(teamPerformance.win) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') GROUP BY champion HAVING count(p) > 4 ORDER BY avg(teamPerformance.win)")
@NamedQuery(name = "Player.getMatchupsTeamGames", query = "SELECT opponent, count(p), avg(teamPerformance.win) FROM Performance p WHERE (teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team is not null AND player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance, player.team HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player = :player AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC)) AND player = :player GROUP BY champion HAVING count(p) > 4 ORDER BY avg(teamPerformance.win)")
@NamedQuery(name = "Player.getMatchupsMatchmade", query = "SELECT opponent, count(p), avg(teamPerformance.win) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start GROUP BY champion HAVING count(p) > 4 ORDER BY avg(teamPerformance.win)")
@NamedQuery(name = "Player.getPresencePRM", query = "SELECT champion, count(s) FROM Selection s WHERE game IN (SELECT teamPerformance FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney' GROUP BY lane ORDER BY count(p)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "Player.getPresencePRMClash", query = "SELECT champion, count(s) FROM Selection s WHERE game IN (SELECT teamPerformance FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') GROUP BY lane ORDER BY count(p)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "Player.getPresenceTeamGames", query = "SELECT champion, count(s) FROM Selection s WHERE (game IN (SELECT teamPerformance.game FROM Performance p1 WHERE player.team is not null AND player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance, player.team HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR game IN (SELECT teamPerformance.game FROM Performance p2 WHERE player = :player AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "Player.getPresenceMatchmade", query = "SELECT champion, count(s) FROM Selection s WHERE game IN (SELECT teamPerformance FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start GROUP BY lane ORDER BY count(p)) GROUP BY champion ORDER BY count(s) desc")
@NamedQuery(name = "Player.getLanePlayedPRM", query = "SELECT lane, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND teamPerformance.game.type = 'tourney' GROUP BY lane ORDER BY count(p) desc")
@NamedQuery(name = "Player.getLanePlayedPRMClash", query = "SELECT lane, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') GROUP BY lane ORDER BY count(p) desc")
@NamedQuery(name = "Player.getLanePlayedTeamGames", query = "SELECT lane, count(p) FROM Performance p WHERE (teamPerformance IN (SELECT teamPerformance FROM Performance p1 WHERE player.team is not null AND player.team = :team AND teamPerformance.game.type = 'ranked' AND teamPerformance.game.start > :start GROUP BY teamPerformance, player.team HAVING COUNT(p1) > 2 ORDER BY COUNT(p1) DESC) OR teamPerformance IN (SELECT teamPerformance FROM Performance p2 WHERE player = :player AND (teamPerformance.game.type = 'tourney' OR teamPerformance.game.type = 'clash') AND teamPerformance.game.start > :start GROUP BY teamPerformance ORDER BY count(p2) DESC)) AND player = :player GROUP BY lane ORDER BY count(p) desc")
@NamedQuery(name = "Player.getLanePlayedMatchmade", query = "SELECT lane, count(p) FROM Performance p WHERE player = :player AND teamPerformance.game.start > :start GROUP BY lane ORDER BY count(p)")
@NamedQuery(name = "Player.fromName", query = "FROM Player WHERE summonerName = :name")
@NamedQuery(name = "Player.fromPuuid", query = "FROM Player WHERE puuid = :puuid")
@NamedQuery(name = "Player.registered", query = "FROM Player WHERE discordUser is not null")
@ExtensionMethod(RankFactory.class)
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
  private DiscordUser discordUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  @ToString.Exclude
  private Team team;

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
  private Set<Rank> ranks;

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
      case RANKED_PLAYER -> !this.ranks.isEmpty();
      case REGISTERED_PLAYER -> this.discordUser != null;
      case TEAM_PLAYER -> this instanceof PrimePlayer && this.getTeam() != null;
    };

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
    return summonerName + " | " + this.getRank();
  }

  public List<Object[]> getLastGames(GameType gameType) {
    return PerformanceFactory.getLastPlayerGames(gameType, this);
  }
}
