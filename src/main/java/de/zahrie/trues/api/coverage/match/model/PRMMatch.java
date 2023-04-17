package de.zahrie.trues.api.coverage.match.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.playday.config.SchedulingRange;
import de.zahrie.trues.api.database.Database;
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
@DiscriminatorValue("prm")
public class PRMMatch extends ScheduleableMatch implements Serializable {
  @Serial
  private static final long serialVersionUID = -6145053153275706756L;

  public static PRMMatch build(Playday matchday, LocalDateTime start, League league, SchedulingRange schedulingRange, Integer matchId) {
    final var match = new PRMMatch(matchday, start, league, schedulingRange, matchId);
    league.getMatches().add(match);
    Database.insert(match);
    Database.update(league);
    return match;
  }

  @Column(name = "match_id")
  private Integer matchId;

  public PRMMatch(Playday matchday, LocalDateTime start, League league, SchedulingRange schedulingRange, Integer matchId) {
    super(matchday, start, league, schedulingRange);
    this.matchId = matchId;
  }

  public PrimeMatchImpl get() {
    return new PrimeMatchImpl(this);
  }
}
