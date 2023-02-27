package de.zahrie.trues.api.coverage.stage;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.coverage.SchedulingMode;
import de.zahrie.trues.models.betting.BetMode;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.season.Season;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_stage")
public class Stage implements Serializable {
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
  private String name;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "stage_start", nullable = false)
  private Calendar start;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "stage_end", nullable = false)
  private Calendar end;

  @Enumerated(EnumType.STRING)
  @Column(name = "bet_mode", length = 10)
  private BetMode betMode;

  @Enumerated(EnumType.STRING)
  @Column(name = "scheduling_mode", length = 9)
  private SchedulingMode schedulingMode;

  @Column(name = "discord_event")
  private Long discordEventId;

  @OneToMany(mappedBy = "stage")
  @ToString.Exclude
  private Set<League> leagues = new LinkedHashSet<>();

  @OneToMany(mappedBy = "stage")
  @ToString.Exclude
  private Set<Playday> playdays = new LinkedHashSet<>();

  public Integer getPrmId() {
    return switch (this.name) {
      case "Kalibrierungsphase" -> 506;
      case "Gruppenphase" -> 509;
      case "Playoffs" -> 512;
      default -> null;
    };
  }

}