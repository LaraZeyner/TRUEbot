package de.zahrie.trues.api.discord.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.calendar.ApplicationCalendar;
import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.discord.notify.NotificationManager;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
  @Setter(AccessLevel.NONE)
  private int messagesSent = 0;

  @Column(name = "msg_digits", nullable = false)
  @Setter(AccessLevel.NONE)
  private int digitsWritten = 0;

  @Column(name = "seconds_online", nullable = false)
  @Setter(AccessLevel.NONE)
  private int secondsOnline = 0;

  @Column(name = "joined")
  @Setter(AccessLevel.NONE)
  private LocalDateTime lastTimeJoined;

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

  @Column(name = "notification", columnDefinition = "SMALLINT UNSIGNED")
  private short notification = 0;

  public void setNotification(short notification) {
    final Integer difference = notification == -1 ? null : this.notification - notification;
    if (difference != null && difference.equals(0)) return;
    this.notification = notification;
    NotificationManager.addNotifiersFor(this,difference);
  }

  public List<DiscordUserGroup> getGroups() {
    return QueryBuilder.hql(DiscordUserGroup.class, "FROM DiscordUserGroup WHERE user = :user").addParameter("user", this).list();
  }

  public List<Membership> getMemberships() {
    return QueryBuilder.hql(Membership.class, "FROM Membership WHERE user = :user").addParameter("user", this).list();
  }

  public List<Membership> getMainMemberships() {
    return QueryBuilder.hql(Membership.class, "FROM Membership WHERE user = :user and role = :role").addParameters(Map.of("user", this, "role", TeamRole.MAIN)).list();
  }

  public List<Application> getApplications() {
    return QueryBuilder.hql(Application.class, "FROM Application WHERE user = :user").addParameter("user", this).list();
  }

  @ToString.Exclude
  @OneToOne(mappedBy = "discordUser")
  private Player player;

  public DiscordUser(long discordId, String mention) {
    this.discordId = discordId;
    this.mention = mention;
  }

  public Member getMember() {
    return Nunu.getInstance().getGuild().getMemberById(discordId);
  }

  public Set<DiscordGroup> getActiveGroups() {
    return getMember().getRoles().stream().map(DiscordGroup::of).collect(Collectors.toSet());
  }

  public void addTempGroups() {
    for (DiscordUserGroup discordUserGroup : getGroups()) {
      if (!discordUserGroup.isActive() && discordUserGroup.getRange().getEndTime().isAfter(LocalDateTime.now())) {
        addGroup(discordUserGroup.getDiscordGroup());
        discordUserGroup.setActive(true);
        Database.update(discordUserGroup);
        Database.update(this);
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
    for (DiscordUserGroup discordUserGroup : getGroups()) {
      if (discordUserGroup.isActive() && !discordUserGroup.getRange().hasEnded()) {
        removeGroup(discordUserGroup.getDiscordGroup());
        discordUserGroup.setActive(false);
        Database.update(discordUserGroup);
        Database.update(this);
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
        .getNickname()).queue(threadChannel -> Database.insert(new ApplicationCalendar(timeRange, "by " + id + " - " + mention, this, threadChannel.getIdLong()))));
    dm("Neuer Termin für Vorstellungsgespräch: " + TimeFormat.DISCORD.of(dateTime));
  }

  public void addSeconds(boolean stillOnline) {
    if (lastTimeJoined != null) {
      final Duration duration = Duration.between(lastTimeJoined, LocalDateTime.now());
      this.secondsOnline += duration.getSeconds();
      this.points += Math.round(duration.getSeconds() / 60.);
    }
    if (stillOnline) this.lastTimeJoined = LocalDateTime.now();
  }

  public void addMessage(String content) {
    this.messagesSent++;
    this.digitsWritten += content.length();
    this.points += content.length();
  }
}
