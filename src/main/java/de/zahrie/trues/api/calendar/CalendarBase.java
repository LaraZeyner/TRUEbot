package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.types.TimeCoverter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
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
@Table(name = "calendar")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "calendar_name")
public class CalendarBase implements Serializable {
  @Serial
  private static final long serialVersionUID = 6429899319470109286L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "calendar_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "calendar_start", nullable = false)
  private Time start;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "calendar_end", nullable = false)
  private Time end;

  @Column(name = "details", length = 1000)
  private String details;

  @Column(name = "weeks")
  private Integer weeks;

  public CalendarBase(Time start, Time end, String details) {
    this.start = start;
    this.end = end;
    this.details = details;
  }
}
