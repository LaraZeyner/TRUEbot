package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
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

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "calendar_start", nullable = false)),
      @AttributeOverride(name = "endTime", column = @Column(name = "calendar_end", nullable = false))
  })
  private TimeRange range;

  @Column(name = "details", length = 1000)
  private String details;

  public CalendarBase(TimeRange timeRange, String details) {
    this.range = timeRange;
    this.details = details;
  }
}
