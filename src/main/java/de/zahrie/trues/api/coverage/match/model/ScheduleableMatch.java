package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
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
@DiscriminatorValue("intern")
public class ScheduleableMatch extends TournamentMatch implements Serializable {
  @Serial
  private static final long serialVersionUID = -2453759856557325436L;

  @Column(name = "scheduling_start")
  private LocalDateTime schedulingStart;

  @Column(name = "scheduling_end")
  private LocalDateTime schedulingEnd;

  public ScheduleableMatch(Playday matchday, LocalDateTime start, League league, LocalDateTime schedulingStart, LocalDateTime schedulingEnd) {
    super(matchday, start, league);
    this.schedulingStart = schedulingStart;
    this.schedulingEnd = schedulingEnd;
  }

}
