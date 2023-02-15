package de.zahrie.trues.models.coverage.match;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

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

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "scheduling_start")
  private Calendar schedulingStart;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "scheduling_end")
  private Calendar schedulingEnd;

}
