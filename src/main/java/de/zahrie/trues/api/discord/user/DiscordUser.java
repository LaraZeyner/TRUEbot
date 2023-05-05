package de.zahrie.trues.api.discord.user;

import java.io.Serial;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.calendar.ApplicationCalendar;
import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.application.ApplicationFactory;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.discord.notify.NotificationManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;


@Getter
@Table("discord_user")
public class DiscordUser implements Entity<DiscordUser> {
  @Serial
  private static final long serialVersionUID = 675455029296764536L;
  private int id;
  private final long discordId; // discord_id
  private String mention; // mention
  private int points = 1000; // points
  private int messagesSent = 0; // msg_count
  private int digitsWritten = 0; // msg_digits
  private int secondsOnline = 0; // seconds_online
  private boolean active = false; // active
  private LocalDateTime lastTimeJoined; // joined
  private DiscordUser acceptedBy; // accepted
  private short notification = 0; // notification
  private LocalDate birthday; // birthday

  public DiscordUser(long discordId, String mention) {
    this.discordId = discordId;
    this.mention = mention;
  }

  public DiscordUser(int id, long discordId, String mention, int points, int messagesSent, int digitsWritten, int secondsOnline, boolean active, LocalDateTime lastTimeJoined, DiscordUser acceptedBy, short notification, LocalDate birthday) {
    this.id = id;
    this.discordId = discordId;
    this.mention = mention;
    this.points = points;
    this.messagesSent = messagesSent;
    this.digitsWritten = digitsWritten;
    this.secondsOnline = secondsOnline;
    this.active = active;
    this.lastTimeJoined = lastTimeJoined;
    this.acceptedBy = acceptedBy;
    this.notification = notification;
    this.birthday = birthday;
  }

  public Player getPlayer() {
    return new Query<Player>().where("discord_user", this).entity();
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public void setMention(String mention) {
    this.mention = mention;
    new Query<DiscordUser>().col("mention", mention).update(id);
  }

  public void setPoints(int points) {
    this.points = points;
    new Query<DiscordUser>().col("points", points).update(id);
  }

  public void setAcceptedBy(DiscordUser acceptedBy) {
    this.acceptedBy = acceptedBy;
    new Query<DiscordUser>().col("accepted", acceptedBy).update(id);
    ApplicationFactory.updateApplicationStatus(this);
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
    new Query<DiscordUser>().col("birthday", birthday).update(id);
  }

  public static DiscordUser get(Object[] objects) {
    return new DiscordUser(
        (int) objects[0],
        (long) objects[1],
        (String) objects[2],
        (int) objects[3],
        (int) objects[4],
        (int) objects[5],
        (int) objects[6],
        (boolean) objects[7],
        (LocalDateTime) objects[8],
        new Query<DiscordUser>().entity(objects[9]),
        (short) objects[10],
        (LocalDate) objects[11]
    );
  }

  @Override
  public DiscordUser create() {
    return new Query<DiscordUser>().key("discord_id", discordId)
        .col("mention", mention).col("points", points).col("msg_count", messagesSent).col("msg_digits", digitsWritten)
        .col("seconds_online", secondsOnline).col("active", active).col("joined", lastTimeJoined).col("accepted", acceptedBy)
        .col("notification", notification).col("birthday", birthday)
        .insert(this);
  }

  public void setNotification(short notification) {
    final Integer difference = notification == -1 ? null : this.notification - notification;
    if (difference != null && difference.equals(0)) return;
    this.notification = notification;
    NotificationManager.addNotifiersFor(this,difference);
    new Query<DiscordUser>().col("notification", notification).update(id);
  }

  public List<DiscordUserGroup> getGroups() {
    return new Query<DiscordUserGroup>().where("discord_user", this).entityList();
  }

  public List<Membership> getMemberships() {
    return new Query<Membership>().where("discord_user", this).entityList();
  }

  public List<Membership> getMainMemberships() {
    return new Query<Membership>().where("discord_user", this).and("role", TeamRole.MAIN).entityList();
  }

  public List<Application> getApplications() {
    return new Query<Application>().where("discord_user", this).entityList();
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
    final var timeRange = new TimeRange(dateTime, Duration.ofMinutes(30));
    Nunu.DiscordChannel.getAdminChannel().sendMessage("Neuer Bewerbungstermin für " + invoker.getMention())
        .queue(message -> message.createThreadChannel("Bewerbung von " + invoker.getMember().getNickname())
            .queue(threadChannel -> new ApplicationCalendar(timeRange, "by " + getId() + " - " + mention, this, threadChannel.getIdLong()).create()));
    dm("Neuer Termin für Vorstellungsgespräch: " + TimeFormat.DISCORD.of(dateTime));
  }

  public void addSeconds(boolean stillOnline) {
    if (lastTimeJoined != null) {
      final Duration duration = Duration.between(lastTimeJoined, LocalDateTime.now());
      this.secondsOnline += duration.getSeconds();
      this.points += Math.round(duration.getSeconds() / 60.);
      new Query<DiscordUser>().col("joined", lastTimeJoined).col("seconds_online", secondsOnline).col("points", points).update(id);
    }
    if (stillOnline) {
      this.lastTimeJoined = LocalDateTime.now();
      new Query<DiscordUser>().col("joined", lastTimeJoined).update(id);
    }
  }

  public void addMessage(String content) {
    this.messagesSent++;
    this.digitsWritten += content.length();
    this.points += content.length();
    new Query<DiscordUser>().col("msg_count", messagesSent).col("msg_digits", digitsWritten).col("points", points).update(id);
  }
}
