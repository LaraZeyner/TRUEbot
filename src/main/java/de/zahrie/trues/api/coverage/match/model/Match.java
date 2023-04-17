package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import de.zahrie.trues.api.coverage.match.MatchResultHandler;
import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.discord.notify.NotificationManager;
import de.zahrie.trues.util.Util;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorFormula;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage", indexes = { @Index(name = "idx_coverage", columnList = "match_id", unique = true) })
@DiscriminatorFormula("IF(coverage_group IS NULL, 'scrimmage', IF(scheduling_start IS NULL, 'bet', IF(match_id IS NULL, 'intern', 'prm')))")
public class Match implements Betable, Serializable, Comparable<Match> {
  @Serial
  private static final long serialVersionUID = -3826796156374823894L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "matchday")
  @ToString.Exclude
  private Playday playday;

  @Column(name = "coverage_start", nullable = false)
  private LocalDateTime start;

  @Column(name = "rate_offset", nullable = false)
  private short rateOffset = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private EventStatus status = EventStatus.CREATED;

  @Column(name = "last_message", nullable = false, length = 1000)
  private String lastMessage = "keine Infos";

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "result", nullable = false, length = 200)
  private String result = "-:-";

  @Enumerated
  @Column(name = "format")
  private MatchFormat format;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "coverage")
  private Set<Participator> participators = new LinkedHashSet<>();

  public void addParticipator(Participator participator) {
    participator.setCoverage(this);
    participators.add(participator);
    Database.update(participator);
    Database.update(this);
  }

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "match")
  private Set<MatchLog> logs;

  public void addLog(MatchLog log) {
    log.setMatch(this);
    logs.add(log);
    Database.update(log);
    Database.update(this);
  }

  public void setStart(LocalDateTime start) {
    if (this.start.equals(start)) return;
    this.start = start;
    handleNotifications();
  }

  public void handleNotifications() {
    if (start.isBefore(LocalDateTime.now().plusDays(1))) {
      participators.stream().filter(participator -> participator.getTeam() != null)
          .filter(participator -> participator.getTeam().getOrgaTeam() != null).forEach(NotificationManager::addNotifiersFor);
    }
  }

  public Participator getHome() {
    return participators.stream().filter(Participator::isFirstPick).findFirst().orElse(null);
  }

  public String getHomeAbbr() {
    return Util.avoidNull(getHome(), "TBD", Participator::getAbbreviation);
  }

  public String getHomeName() {
    return Util.avoidNull(getHome(), "TBD", participator -> participator.getTeam().getName());
  }

  public String getMatchup() {
    return getHomeName() + " vs. " + getGuestName();
  }

  public Participator getGuest() {
    return participators.stream().filter(team -> !team.isFirstPick()).findFirst().orElse(null);
  }

  public String getGuestAbbr() {
    return Util.avoidNull(getGuest(), "TBD", Participator::getAbbreviation);
  }

  public String getGuestName() {
    return Util.avoidNull(getGuest(), "TBD", participator -> participator.getTeam().getName());
  }

  public String getExpectedResult() {
    return getResultHandler().expectResultOf(this) + (isRunning() ? "*" : "");
  }

  public Participator getOpponent(Team team) {
    if (participators.stream().map(Participator::getTeam).noneMatch(team1 -> team1.equals(team))) return null;
    return participators.stream().filter(participator -> !participator.getTeam().equals(team)).findFirst().orElse(null);
  }

  public Participator getParticipator(Team team) {
    if (participators.stream().map(Participator::getTeam).noneMatch(team1 -> team1.equals(team))) return null;
    return participators.stream().filter(participator -> participator.getTeam().equals(team)).findFirst().orElse(null);
  }

  public Match(Playday playday, LocalDateTime start) {
    this.playday = playday;
    this.start = start;
    this.format = playday.getFormat();
  }

  public Team getOpponentOf(Team team) {
    return participators.stream().map(Participator::getTeam).filter(t -> !t.equals(team)).findFirst().orElse(null);
  }

  public void updateResult(String result) {
    MatchResultHandler.fromResultString(result).update(this);
  }

  public MatchResultHandler getResultHandler() {
    return MatchResultHandler.fromResultString(this.result);
  }


  public boolean isOrgagame() {
    return Stream.of(getHome(), getGuest()).anyMatch(participator -> participator.getTeam().getOrgaTeam() != null);
  }

  public void addParticipators(Participator home, Participator guest) {
    if (getParticipators().contains(home) && getParticipators().contains(guest)) return;

    if (this instanceof PRMMatch) {
      Stream.of(home, guest).map(Participator::getTeam).forEach(team -> team.setHighlight(true));
    }

    if (isOrgagame()) {
      Stream.of(home, guest)
          .map(Participator::getTeam)
          .forEach(team -> team.refresh(getStart()));
    }
    if (!home.getTeam().equals(getHome().getTeam())) addParticipator(home);
    if (!guest.getTeam().equals(getGuest().getTeam())) addParticipator(guest);

    handleNotifications();
  }

  @Override
  public int compareTo(@NotNull Match o) {
    return Comparator.comparing(Match::getStart).compare(this, o);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final Match match)) return false;
    return getId() == match.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  public String getTypeString() {
    if (this instanceof Scrimmage) return "Scrimmage";
    if (this instanceof OrgaCupMatch) return "TRUE-Cup";
    if (this instanceof PRMMatch) return "Prime League";
   else return "Sportwette";
  }

  public boolean isRunning() {
    return result.equals("-:-");
  }

  public TimeRange getExpectedTimeRange() {
    return new TimeRange(start, format.getDuration(), ChronoUnit.MINUTES);
  }

}
