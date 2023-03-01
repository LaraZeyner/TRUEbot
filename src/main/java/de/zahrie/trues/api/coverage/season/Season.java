package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_season",
        indexes = { @Index(name = "season_full", columnList = "season_full", unique = true),
                @Index(name = "season_name", columnList = "season_name", unique = true),
                @Index(name = "season_id", columnList = "season_id", unique = true) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "department", discriminatorType = DiscriminatorType.STRING)
public class Season implements Betable, Seasonable, Serializable {
  @Serial
  private static final long serialVersionUID = 3263600626506335102L;

  private final CoverageDepartment coverageDepartment =
      CoverageDepartment.Scrimmage;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_season_id", columnDefinition = "TINYINT UNSIGNED not null")
  private short id;
  @Column(name = "season_name", nullable = false, length = 15)
  private String name;
  @Column(name = "season_full", length = 25)
  private String fullName;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "season_start", nullable = false)
  private Calendar start;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "season_end", nullable = false)
  private Calendar end;
  @OneToMany(mappedBy = "season")
  @ToString.Exclude
  private Set<Stage> stages = new LinkedHashSet<>();

  @NonNull
  public PlayStage getStageOfId(int id) {
    for (Stage stage : this.stages) {
      final var playStage = (PlayStage) stage;
      if ((playStage.pageId() == id)) {
        return Objects.requireNonNull(playStage);
      }
    }
    throw new NullPointerException("Stage cannot be null");
  }

  @Override
  public CoverageDepartment type() {
    return null;
  }
}
