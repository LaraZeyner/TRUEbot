package de.zahrie.trues.api.coverage.season;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.SignupStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.util.Util;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "coverage_season",
        indexes = { @Index(name = "season_full", columnList = "season_full", unique = true),
                @Index(name = "season_name", columnList = "season_name", unique = true),
                @Index(name = "season_id", columnList = "season_id", unique = true) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "department", discriminatorType = DiscriminatorType.STRING)
public class Season implements Betable, Serializable, Comparable<Season> {
  @Serial
  private static final long serialVersionUID = 3263600626506335102L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_season_id", columnDefinition = "TINYINT UNSIGNED not null")
  @EqualsAndHashCode.Include
  private short id;

  @Column(name = "season_name", nullable = false, length = 15)
  private String name;

  @Column(name = "season_full", length = 25)
  private String fullName;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "season_start", nullable = false)),
      @AttributeOverride(name = "endTime", column = @Column(name = "season_end", nullable = false))
  })
  private TimeRange range;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @OneToMany(mappedBy = "season")
  @ToString.Exclude
  private Set<Stage> stages = new LinkedHashSet<>();

  @NonNull
  public PlayStage getStageOfId(int id) {
    for (Stage stage : this.stages) {
      final var playStage = (PlayStage) stage;
      if ((playStage.pageId() == id)) return Util.nonNull(playStage);
    }
    throw new NullPointerException("Stage cannot be null");
  }

  @Override
  public int compareTo(@NotNull Season o) {
    return Comparator.comparing(Season::getId).compare(this, o);
  }

  public String getSignupStatusForTeam(Team team) {
    if (team.getSignups().stream().anyMatch(signup -> signup.getSeason().equals(this))) return "angemeldet";
    return stages.stream().filter(stage -> stage instanceof SignupStage).findFirst()
        .map(stage -> stage.getStart().isBefore(LocalDateTime.now()) ? "Anmeldung gestartet" : "Anmeldung " +
            TimeFormat.DISCORD.of(stage.getStart()))
        .orElse("keine Anmeldung eingerichtet");
  }
}
