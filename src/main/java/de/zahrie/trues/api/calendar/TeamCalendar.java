package de.zahrie.trues.api.calendar;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
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
  @Column(name = "calendar_type", nullable = false, length = 11)
  private TeamCalendarType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "orga_team")
  @ToString.Exclude
  private OrgaTeam orgaTeam;

  @Column(name = "thread_id")
  private Long threadId;

  public TeamCalendar(TimeRange timeRange, String details, TeamCalendarType type, OrgaTeam orgaTeam, long threadId) {
    super(timeRange, details);
    this.type = type;
    this.orgaTeam = orgaTeam;
    this.threadId = threadId;
  }

  public enum TeamCalendarType {
    KALIBRIERUNG, COACHING, CLASH, MEETING, TRAINING
  }
}
