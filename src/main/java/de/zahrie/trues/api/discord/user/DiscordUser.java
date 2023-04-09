package de.zahrie.trues.api.discord.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.calendar.ApplicationCalendar;
import de.zahrie.trues.api.calendar.UserCalendar;
import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.betting.Bet;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.util.Nunu;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.Member;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "discord_user", indexes = {@Index(name = "discord_id", columnList = "discord_id", unique = true)})
@ExtensionMethod({DiscordUserFactory.class})
public class DiscordUser implements Serializable {
  @Serial
  private static final long serialVersionUID = -2575760126811506041L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "discord_user_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "discord_id", nullable = false)
  private long discordId;

  @Column(name = "discord_mention", nullable = false)
  private String mention;

  @Column(name = "msg_count", columnDefinition = "SMALLINT UNSIGNED not null")
  private int messagesSent = 0;

  @Column(name = "msg_digits", nullable = false)
  private int digitsWritten = 0;

  @Column(name = "seconds_online", nullable = false)
  private int secondsOnline = 0;

  @Column(name = "joined")
  private LocalDateTime lastTimeJoined;
  //TODO (Abgie) 08.04.2023: joined

  @Column(name = "birthday")
  private LocalDate birthday;

  @Column(name = "points", nullable = false)
  private int points = 1000;

  @Column(name = "active", nullable = false)
  private boolean isActive = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "accepted")
  @ToString.Exclude
  private DiscordUser acceptedBy;

  private boolean isAccepted;

  @Column(name = "notification", columnDefinition = "SMALLINT UNSIGNED")
  private short notification = 0;

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  private Set<DiscordUserGroup> groups;

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  private Set<Membership> memberships = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  private Set<Application> applications = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  private Set<UserCalendar> calendarEntries = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  @ToString.Exclude
  private Set<Bet> bets = new LinkedHashSet<>();

  @ToString.Exclude
  @OneToOne(mappedBy = "discordUser")
  private Player player;

  public Member getMember() {
    return Nunu.getInstance().getGuild().getMemberById(discordId);
  }

  public Set<DiscordGroup> getActiveGroups() {
    return getMember().getRoles().stream().map(DiscordGroup::of).collect(Collectors.toSet());
  }

  public void addTempGroups() {
    for (DiscordUserGroup discordUserGroup : groups) {
      if (!discordUserGroup.isActive() && discordUserGroup.getPermissionEnd().isAfter(LocalDateTime.now())) {
        addGroup(discordUserGroup.getDiscordGroup());
        discordUserGroup.setActive(true);
        Database.save(discordUserGroup);
        Database.save(this);
      }
    }
  }

  public void addGroup(DiscordGroup group) {
    addGroup(group, LocalDateTime.now(), 0);
  }

  public void addGroup(DiscordGroup group, LocalDateTime start, int days) {
    new RoleGranter(this).add(group, start, days);
  }

  public void removeTempGroups() {
    for (DiscordUserGroup discordUserGroup : groups) {
      if (discordUserGroup.isActive() && discordUserGroup.getPermissionEnd().isAfter(LocalDateTime.now())) {
        removeGroup(discordUserGroup.getDiscordGroup());
        discordUserGroup.setActive(false);
        Database.save(discordUserGroup);
        Database.save(this);
      }
    }
  }

  public void removeGroup(DiscordGroup group) {
    new RoleGranter(this).remove(group);
  }

  public boolean isAbove(DiscordGroup group) {
    return getActiveGroups().stream().anyMatch(group::isAbove);
  }

  public boolean isEvenOrAbove(DiscordGroup group) {
    return getActiveGroups().contains(group) || getActiveGroups().stream().anyMatch(group::isAbove);
  }

  public void dm(String content) {
    getMember().getUser().openPrivateChannel()
        .flatMap(privateChannel -> privateChannel.sendMessage(content))
        .queue();
  }

  public void schedule(LocalDateTime dateTime, DiscordUser invoker) {
    this.acceptedBy = invoker;
    final var timeRange = new TimeRange(dateTime, 30, ChronoUnit.MINUTES);
    Nunu.DiscordChannel.getAdminChannel().sendMessage("Neuer Bewerbungstermin für " + invoker.getMention()).queue(message -> message.createThreadChannel("Bewerbung von " + invoker.getMember()
        .getNickname()).queue(threadChannel -> Database.save(new ApplicationCalendar(timeRange, "by " + id + " - " + mention, this, threadChannel.getIdLong()))));
    dm("Neuer Termin für Vorstellungsgespräch: " + TimeFormat.DISCORD.of(dateTime));
  }
}
