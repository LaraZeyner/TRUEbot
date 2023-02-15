package de.zahrie.trues.models.coverage.match;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.models.betting.Bet;
import de.zahrie.trues.models.coverage.EventStatus;
import de.zahrie.trues.models.coverage.Participator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
public class Match implements Serializable {
  @Serial
  private static final long serialVersionUID = -3826796156374823894L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "matchday", columnDefinition = "TINYINT UNSIGNED not null")
  private short matchday;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "coverage_start", nullable = false)
  private Calendar start;

  @Column(name = "rate_offset", nullable = false)
  private short rateOffset;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private EventStatus status;

  @Column(name = "last_message", nullable = false, length = 1000)
  private String lastMessage = "keine Infos";

  @Column(name = "active", nullable = false)
  private boolean isActive = true;

  @Column(name = "result", nullable = false, length = 200)
  private String result = "-:-";

  @Column(name = "log_entries", columnDefinition = "TINYINT UNSIGNED not null")
  private short logEntryAmount = 0;

  @OneToMany(mappedBy = "coverage")
  @ToString.Exclude
  private Set<Participator> teams = new LinkedHashSet<>();

  @OneToMany(mappedBy = "coverage")
  @ToString.Exclude
  private Set<Bet> bets = new LinkedHashSet<>();

}
