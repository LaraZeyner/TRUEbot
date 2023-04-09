package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.coverage.stage.StageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "coverage_stage")
@DiscriminatorFormula("stage_name")
public class Stage implements Serializable, Comparable<Stage> {
  @Serial
  private static final long serialVersionUID = 8688201396748655675L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_stage_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "season", nullable = false)
  @ToString.Exclude
  private Season season;

  @Enumerated(EnumType.STRING)
  @Column(name = "stage_name", nullable = false, length = 25)
  private StageType name;

  @Column(name = "stage_start", nullable = false)
  private LocalDateTime start;

  @Column(name = "stage_end", nullable = false)
  private LocalDateTime end;

  @Column(name = "discord_event")
  private Long discordEventId;

  public boolean isBetable() {
    // TODO (Abgie) 15.03.2023: never used
    return this instanceof Betable;
  }

  @Override
  public int compareTo(@NotNull Stage o) {
    return Comparator.comparing(Stage::getStart).compare(this, o);
  }
}
