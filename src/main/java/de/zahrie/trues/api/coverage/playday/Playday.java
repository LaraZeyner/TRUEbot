package de.zahrie.trues.api.coverage.playday;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import de.zahrie.trues.api.coverage.stage.Stage;
import de.zahrie.trues.api.coverage.match.model.Match;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
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
@Table(name = "coverage_playday", indexes = {
        @Index(name = "idx_playday_2", columnList = "stage, playday_index", unique = true) })
@NamedQuery(name = "Playday.fromStageAndId", query = "FROM Playday WHERE stage = :stage AND idx = :id")
@NamedQuery(name = "Playday.fromStageAndStart", query = "FROM Playday WHERE stage = :stage AND start = :start")
public class Playday implements Serializable {
  @Serial
  private static final long serialVersionUID = -1118100065150854452L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coverage_playday_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage", nullable = false)
  @ToString.Exclude
  private Stage stage;

  @Column(name = "playday_index", columnDefinition = "TINYINT UNSIGNED not null")
  private short idx;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "playday_start", nullable = false)
  private Calendar start;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "playday_end", nullable = false)
  private Calendar end;

  @OneToMany(mappedBy = "matchday")
  @ToString.Exclude
  private Set<Match> matches;

  public Playday(Stage stage, short idx) {
    this.stage = stage;
    this.idx = idx;
    this.start = stage.getStart();
    if (start.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
      final LocalDate date = LocalDate.of(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH)).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
      start = Calendar.getInstance();
      start.setTime(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
    start.set(Calendar.HOUR_OF_DAY, 0);
    start.set(Calendar.MINUTE, 0);
    start.set(Calendar.SECOND, 0);
    start.add(Calendar.WEEK_OF_YEAR, idx - 1);
    this.end = Calendar.getInstance();
    end.setTime(start.getTime());
    end.add(Calendar.DATE, 6);
    end.set(Calendar.HOUR_OF_DAY, 23);
    end.set(Calendar.MINUTE, 59);
  }
}