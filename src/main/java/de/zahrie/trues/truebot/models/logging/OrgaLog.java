package de.zahrie.trues.truebot.models.logging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "orga_log")
public class OrgaLog implements Serializable {
  @Serial
  private static final long serialVersionUID = 5192094953488170552L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id", columnDefinition = "SMALLINT UNSIGNED not null")
  private int id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "log_time", nullable = false)
  private Calendar timestamp = Calendar.getInstance();

  @Enumerated(EnumType.STRING)
  @Column(name = "department", nullable = false, length = 25)
  private LogDepartment department;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 50)
  private LogAction action;

  @Enumerated(EnumType.STRING)
  @Column(name = "invoker_type", nullable = false, length = 25)
  private UserType invokerType;

  @Column(name = "invoker", nullable = false, length = 50)
  private String invoker;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", length = 25)
  private UserType targetType;

  @Column(name = "target", length = 50)
  private String target;

  @Column(name = "team")
  private Integer team;

  @Column(name = "details", nullable = false, length = 1000)
  private String details = "-";

  @Column(name = "to_send", nullable = false)
  private boolean toSend = false;

}