package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
public class ScheduleableMatch extends TournamentMatch implements Serializable {
  @Serial
  private static final long serialVersionUID = -2453759856557325436L;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "scheduling_start", nullable = false)),
      @AttributeOverride(name = "endTime", column = @Column(name = "scheduling_end", nullable = false))
  })
  private TimeRange range;

  public ScheduleableMatch(Playday matchday, LocalDateTime start, League league, TimeRange timeRange) {
    super(matchday, start, league);
    this.range = timeRange;
  }

}
