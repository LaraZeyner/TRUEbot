package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.config.AbstractTimeRange;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.types.TimeCoverter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("intern")
public class ScheduleableMatch extends TournamentMatch implements Serializable, AbstractTimeRange {
  @Serial
  private static final long serialVersionUID = -2453759856557325436L;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "scheduling_start")
  private Time schedulingStart;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "scheduling_end")
  private Time schedulingEnd;

  @Override
  public Time start() {
    return new Time(schedulingStart);
  }

  @Override
  public Time end() {
    return new Time(schedulingEnd);
  }

  public ScheduleableMatch(Playday matchday, Time start, League league, Time schedulingStart, Time schedulingEnd) {
    super(matchday, start, league);
    this.schedulingStart = schedulingStart;
    this.schedulingEnd = schedulingEnd;
  }

}
