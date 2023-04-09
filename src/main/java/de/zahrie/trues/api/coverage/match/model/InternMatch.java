package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
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
public class InternMatch extends ScheduleableMatch implements Serializable {
  @Serial
  private static final long serialVersionUID = -6145053153275706756L;

  @Column(name = "match_id")
  private Integer internId;

  public InternMatch(Playday matchday, LocalDateTime start, League league, TimeRange timeRange, Integer matchId) {
    super(matchday, start, league, timeRange);
    this.internId = matchId;
  }
}
