package de.zahrie.trues.models.calendar;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

import jakarta.persistence.Column;
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
import org.hibernate.annotations.DiscriminatorFormula;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "calendar")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("IF(discord_user IS NULL, 'team', " +
    "IF(calendar_name = 'Scheduling', IF(repeated = 1, 'plan_repeated', 'plan'), " +
    "IF(calendar_type = 'Bewerbung', 'user_app', 'user')))")
public class CalendarBase implements Serializable {
  @Serial
  private static final long serialVersionUID = 6429899319470109286L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "calendar_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Column(name = "calendar_name", nullable = false, length = 200)
  private String name;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "calendar_start", nullable = false)
  private Calendar start;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "calendar_end", nullable = false)
  private Calendar end;

}