package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.coverage.stage.StageType;
import de.zahrie.trues.api.coverage.stage.Stageable;
import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.api.datatypes.calendar.Time;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_stage")
@DiscriminatorFormula("stage_name")
public class Stage implements Serializable, Stageable {
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

  @Column(name = "stage_name", nullable = false, length = 25)
  @Enumerated(EnumType.STRING)
  private StageType name;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "stage_start", nullable = false)
  private Time start;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "stage_end", nullable = false)
  private Time end;

  @Column(name = "discord_event")
  private Long discordEventId;

  public boolean isBetable() {
    // TODO (Abgie) 15.03.2023: never used
    return this instanceof Betable;
  }

  @Override
  public StageType type() {
    return null;
  }

}
