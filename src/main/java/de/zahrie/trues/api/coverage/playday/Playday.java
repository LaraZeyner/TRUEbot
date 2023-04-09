package de.zahrie.trues.api.coverage.playday;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.playday.config.PlaydayRange;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_playday", indexes = @Index(name = "idx_playday_2", columnList = "stage, playday_index", unique = true))
public class Playday implements Serializable, Comparable<Playday> {
  @Serial
  private static final long serialVersionUID = -1118100065150854452L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_playday_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage", nullable = false)
  @ToString.Exclude
  private PlayStage stage;

  @Column(name = "playday_index", columnDefinition = "TINYINT UNSIGNED not null")
  private short idx;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "playday_start", nullable = false)),
      @AttributeOverride(name = "endTime", column = @Column(name = "playday_end", nullable = false))
  })
  private TimeRange range;

  @OneToMany(mappedBy = "playday")
  @ToString.Exclude
  private Set<Match> matches;

  public Playday(PlayStage stage, short index, PlaydayRange playdayRange) {
    this.stage = stage;
    this.idx = index;
    this.range = playdayRange;
  }

  @Override
  public int compareTo(@NotNull Playday o) {
    return Comparator.comparing(Playday::getRange).compare(this, o);
  }
}
