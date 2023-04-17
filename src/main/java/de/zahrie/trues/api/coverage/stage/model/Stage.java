package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_stage")
@DiscriminatorColumn(name = "stage_name")
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

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "stage_start", nullable = false)),
      @AttributeOverride(name = "endTime", column = @Column(name = "stage_end", nullable = false))
  })
  private TimeRange range;

  @Column(name = "discord_event")
  private Long discordEventId;

  public boolean isBetable() {
    // TODO (Abgie) 15.03.2023: never used
    return this instanceof Betable;
  }

  @Override
  public int compareTo(@NotNull Stage o) {
    return Comparator.comparing(Stage::getRange).compare(this, o);
  }

  public String type() {
    if (this instanceof SignupStage) return "Anmeldung";
    if (this instanceof CalibrationStage) return "Kalibrierungsphase";
    if (this instanceof CreationStage) return "Auslosung";
    if (this instanceof GroupStage) return "Gruppenphase";
    if (this instanceof PlayoffStage) return "Playoffs";
    return null;
  }

  @RequiredArgsConstructor
  @Getter
  public enum StageType {
    SIGNUP_STAGE(SignupStage.class, 6, "Anmeldung", null),
    CALIBRATION_STAGE(CalibrationStage.class, 5, "Kalibrierungsphase", 506),
    CREATION_STAGE(CreationStage.class, 6, "Auslosung", null),
    GROUP_STAGE(GroupStage.class, 5, "Gruppenphase", 509),
    PLAYOFF_STAGE(PlayoffStage.class, 5, "Playoffs", 512),

    PLAY_STAGE(PlayStage.class, -1, "spielen", null),
    WAITING_STAGE(WaitingStage.class, -1, "warten", null);

    @Nullable
    public static StageType fromClass(Class<? extends Stage> clazz) {
      return Arrays.stream(StageType.values()).filter(stageType -> stageType.getEntityClass().equals(clazz)).findFirst().orElse(null);
    }

    @Nullable
    public static StageType fromName(String name) {
      return Arrays.stream(StageType.values()).filter(stageType -> stageType.getDisplayName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Nullable
    public static StageType fromPrmId(int prmId) {
      if (prmId < 1) return null;
      return Arrays.stream(StageType.values()).filter(stageType -> stageType.getPrmId() == prmId).findFirst().orElse(null);
    }

    private final Class<? extends Stage> entityClass;
    @Getter(AccessLevel.NONE)
    private final int subTypeId;
    private final String displayName;
    private final Integer prmId;

    public StageType getSubType() {
      return subTypeId == -1 ? null : StageType.values()[subTypeId];
    }
  }
}
