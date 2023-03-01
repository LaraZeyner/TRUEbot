package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import de.zahrie.trues.api.coverage.match.log.EventStatus;
import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.models.betting.Bet;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.util.database.Database;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@ToString
@Entity
@Table(name = "coverage",
        indexes = { @Index(name = "idx_coverage", columnList = "match_id", unique = true) })
@DiscriminatorFormula("IF(coverage_group IS NULL, 'scrimmage', IF(scheduling_start IS NULL, 'bet', IF(match_id IS NULL, 'intern', 'prm')))")
public class Match implements Betable, Serializable {
  @Serial
  private static final long serialVersionUID = -3826796156374823894L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "matchday")
  @ToString.Exclude
  private Playday matchday;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "coverage_start", nullable = false)
  private Calendar start;

  @Column(name = "rate_offset", nullable = false)
  private short rateOffset = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private EventStatus status = EventStatus.created;

  @Column(name = "last_message", nullable = false, length = 1000)
  private String lastMessage = "keine Infos";

  @Column(name = "active", nullable = false)
  private boolean isActive = true;

  @Column(name = "result", nullable = false, length = 200)
  private String result = "-:-";

  @OneToMany(mappedBy = "coverage")
  @ToString.Exclude
  private Set<Participator> participators = new LinkedHashSet<>();

  @OneToMany(mappedBy = "coverage")
  @ToString.Exclude
  private Set<Bet> bets = new LinkedHashSet<>();

  @OneToMany(mappedBy = "match")
  @ToString.Exclude
  private Set<MatchLog> logs = new LinkedHashSet<>();

  public Participator getHome() {
    return participators.stream().filter(Participator::isFirstPick).findFirst().orElse(null);
  }

  public Participator getGuest() {
    return participators.stream().filter(team -> !team.isFirstPick()).findFirst().orElse(null);
  }

  public Match(Playday matchday, Calendar start) {
    this.matchday = matchday;
    this.start = start;
  }

  public void setResult(String result) {
    final String scoreTeam1 = result.split(":")[0];
    final int score1 = scoreTeam1.equals("-") ? 0 : Integer.parseInt(scoreTeam1);
    final Participator home = getHome();
    if (home != null) {
      home.setWins((short) score1);
      Database.save(home);
    }

    final String scoreTeam2 = result.split(":")[1];
    final int score2 = scoreTeam2.equals("-") ? 0 : Integer.parseInt(scoreTeam2);
    final Participator guest = getGuest();
    if (guest != null) {
      guest.setWins((short) score2);
      Database.save(guest);
    }

    this.result = result;
  }

  public boolean isOrgagame() {
    return Stream.of(getHome(), getGuest()).anyMatch(participator -> participator.getTeam().getOrgaTeam() != null);
  }

  public void addParticipators(Participator home, Participator guest) {
    if (this instanceof PrimeMatch) {
      Stream.of(home, guest).map(Participator::getTeam).forEach(team -> team.setHighlight(true));
    }

    if (isOrgagame()) {
      Stream.of(home, guest).map(Participator::getTeam).filter(Team::isNotOrgaTeam).forEach(team -> team.refresh(this.getStart()));
    }

    if (!home.getTeam().equals(getHome().getTeam())) {
      home.setCoverage(this);
      this.getParticipators().add(home);
      Database.save(home);
    }

    if (!guest.getTeam().equals(getGuest().getTeam())) {
      guest.setCoverage(this);
      this.getParticipators().add(guest);
      Database.save(guest);
    }

    Database.save(this);
  }
}
