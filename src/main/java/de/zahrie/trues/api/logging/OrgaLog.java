package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.database.types.TimeCoverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "orga_log")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "department")
public class OrgaLog implements Serializable {
  @Serial
  private static final long serialVersionUID = 8755701870577684772L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "log_time", nullable = false)
  private Time timestamp = new Time();

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "invoking_user")
  @ToString.Exclude
  private DiscordUser invoker;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "target_user", nullable = false)
  @ToString.Exclude
  private DiscordUser target;

  @Column(name = "details", nullable = false, length = 1000)
  private String details = "-";

  public OrgaLog(DiscordUser target, String details) {
    this(null, target, details);
  }

  public OrgaLog(DiscordUser invoker, DiscordUser target, String details) {
    this.invoker = invoker;
    this.target = target;
    this.details = details;
  }
}
