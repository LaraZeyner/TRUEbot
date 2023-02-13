package de.zahrie.trues.truebot.models.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.truebot.models.community.OrgaTeam;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("team")
public class TeamCalendar extends CalendarBase implements Serializable {
  @Serial
  private static final long serialVersionUID = 8384587779953917815L;

  @Enumerated(EnumType.STRING)
  @Column(name = "calendar_type", nullable = false, length = 200)
  private TeamCalendarType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "orga_team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Column(name = "details", length = 1000)
  private String details;

}