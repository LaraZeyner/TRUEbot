package de.zahrie.trues.models.discord.member;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.datatypes.calendar.Day;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.types.DayConverter;
import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.models.betting.Bet;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.discord.DiscordGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeRegistration;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discord_user", indexes = { @Index(name = "discord_id", columnList = "discord_id", unique = true) })
@TypeRegistration(basicClass = Time.class, userType = TimeCoverter.class)
public class DiscordMember implements Serializable {
  @Serial
  private static final long serialVersionUID = -2575760126811506041L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "discord_user_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "discord_id", nullable = false)
  private long discordId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "perm_group")
  @ToString.Exclude
  private DiscordGroup highestGroup;

  @Column(name = "msg_count", columnDefinition = "SMALLINT UNSIGNED not null")
  private int messagesSent = 0;

  @Column(name = "msg_digits", nullable = false)
  private int digitsWritten = 0;

  @Column(name = "seconds_online", nullable = false)
  private int secondsOnline = 0;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "joined")
  private Time lastTimeJoined;

  @Temporal(TemporalType.DATE)
  @Convert(converter = DayConverter.class)
  @Column(name = "birthday")
  private Day birthday;

  @Column(name = "points", nullable = false)
  private int points = 1000;

  @Column(name = "active", nullable = false)
  private boolean isActive = false;

  @Column(name = "notification", columnDefinition = "SMALLINT UNSIGNED")
  private Integer notification = 0;

  @OneToMany(mappedBy = "member")
  @ToString.Exclude
  private Set<OrgaMember> apps = new LinkedHashSet<>();

  @OneToMany(mappedBy = "member")
  @ToString.Exclude
  private Set<Bet> bets = new LinkedHashSet<>();

  @ToString.Exclude
  @OneToOne(mappedBy = "member")
  private Player player;

}