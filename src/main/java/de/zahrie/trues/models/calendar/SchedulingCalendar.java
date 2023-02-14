package de.zahrie.trues.models.calendar;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@DiscriminatorValue("plan")
public class SchedulingCalendar extends UserCalendar implements Serializable {

  @Serial
  private static final long serialVersionUID = 7699790641205618454L;

  @Enumerated(EnumType.STRING)
  @Column(name = "calendar_type", nullable = false)
  private SchedulingCalendarType type;

}