package de.zahrie.trues.api.coverage.participator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.model.Team;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Team als Teilnehmer an einem Match
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_team", indexes = {
        @Index(name = "idx_coverage_team_2", columnList = "coverage, team", unique = true),
        @Index(name = "idx_coverage_team", columnList = "coverage, first", unique = true) })
@NamedQuery(name = "Participator.nextForTeam", query = "FROM Participator WHERE team = :team AND coverage.result = '-:-' ORDER BY coverage")
@NamedQuery(name = "Participator.lastForTeam", query = "FROM Participator WHERE team = :team AND coverage.result <> '-:-' ORDER BY coverage desc")
public class Participator implements Serializable, Comparable<Participator> {
  @Serial
  private static final long serialVersionUID = 738958738264529474L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_team_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "coverage", nullable = false)
  @ToString.Exclude
  private Match coverage;

  @Column(name = "first", nullable = false)
  private boolean isFirstPick = false;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "team")
  @ToString.Exclude
  private Team team;

  @Column(name = "wins", columnDefinition = "TINYINT UNSIGNED not null")
  private short wins = 0;

  @Column(name = "lineup_fixed", nullable = false)
  private boolean lineupFixed = false;

  @Embedded
  private ParticipatorRoute route;

  @Column(name = "discord_event")
  private Long discordEventId;

  @Column(name = "discord_message")
  private Long messageId;

  @OneToMany(mappedBy = "participator")
  @ToString.Exclude
  private Set<Lineup> lineups = new LinkedHashSet<>();

  @OneToMany(mappedBy = "participator")
  @ToString.Exclude
  private Set<MatchLog> logs = new LinkedHashSet<>();

  public Participator(boolean isFirstPick, Team team) {
    this.isFirstPick = isFirstPick;
    this.team = team;
  }

  public ParticipatorImpl get() {
    return new ParticipatorImpl(this);
  }

  @Override
  public int compareTo(@NotNull Participator o) {
    return Comparator.comparing(Participator::getCoverage).compare(this, o);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final Participator that)) return false;
    if (id == that.getId()) return true;
    return Objects.equals(getCoverage(), that.getCoverage()) && Objects.equals(getTeam(), that.getTeam());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getCoverage(), getTeam());
  }
}
