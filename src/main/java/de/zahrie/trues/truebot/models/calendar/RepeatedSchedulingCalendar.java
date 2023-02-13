package de.zahrie.trues.truebot.models.calendar;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

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
@DiscriminatorValue("plan_repeated")
public class RepeatedSchedulingCalendar extends SchedulingCalendar implements Serializable {
  @Serial
  private static final long serialVersionUID = 8384587779953917815L;

  @Column(name = "repeat_end")
  private Calendar repeatEnd;

  @Column(name = "repeat_interval")
  private Integer repeatInterval;

  @Column(name = "repeat_year", length = 10)
  private String repeatYear;

  @Column(name = "repeat_month", length = 10)
  private String repeatMonth;

  @Column(name = "repeat_monthday", length = 10)
  private String repeatMonthday;

  @Column(name = "repeat_week", length = 10)
  private String repeatWeek;

  @Column(name = "repeat_weekday", length = 10)
  private String repeatWeekday;

}