package de.zahrie.trues.api.coverage.match.log;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.util.database.Database;
import de.zahrie.trues.util.util.Time;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Lara on 16.02.2023 for TRUEbot
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "coverage_log")
@DiscriminatorColumn(name = "action")
public class MatchLog implements Serializable {
  @Serial
  private static final long serialVersionUID = 7775661777098550144L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "log_time", nullable = false)
  private Calendar timestamp = Calendar.getInstance();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coverage")
  @ToString.Exclude
  private Match match;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 50, insertable=false, updatable=false)
  private MatchLogAction action;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coverage_team")
  @ToString.Exclude
  private Participator participator;

  @Column(name = "details", nullable = false, length = 1000)
  private String details = "-";

  @Column(name = "to_send", nullable = false)
  private boolean toSend = true;

  public MatchLog(Calendar timestamp, MatchLogAction action, Match match, String details) {
    this.timestamp = timestamp;
    this.action = action;
    this.match = match;
    this.details = details;
  }

  public MatchLog handleTeam(String content) {
    final MatchLog log = LogFactory.handleUserWithTeam(this, content);
    if (this.participator != null) {
      Database.save(this.participator);
    }
    return log;
  }

  public Participator getParticipator() {
    return participator != null ? participator : new Participator(false, new Team("Administration", "Admin"));
  }

  public Time getTimestamp() {
    return new Time(timestamp);
  }
}
