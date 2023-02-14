package de.zahrie.trues.models.calendar;

import java.io.Serial;
import java.io.Serializable;

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
@DiscriminatorValue("user_app")
public class ApplicationCalendar extends UserCalendar implements Serializable {
  @Serial
  private static final long serialVersionUID = 8384587779953917815L;

  @Column(name = "details", length = 1000)
  private String details;

}