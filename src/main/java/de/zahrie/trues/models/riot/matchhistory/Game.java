package de.zahrie.trues.models.riot.matchhistory;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.database.types.TimeCoverter;
import de.zahrie.trues.api.datatypes.calendar.Time;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
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
@Table(name = "game",
        indexes = { @Index(name = "game_idx_orgagame", columnList = "orgagame") })
public class Game implements Serializable {
  @Serial
  private static final long serialVersionUID = -2880626253889374458L;


  @Id
  @Column(name = "game_id", nullable = false, length = 16)
  private String id;

  @Temporal(TemporalType.TIMESTAMP)
  @Type(TimeCoverter.class)
  @Column(name = "start_time", nullable = false)
  private Time start;

  @Column(name = "duration", columnDefinition = "SMALLINT UNSIGNED not null")
  private int durationInSeconds;

  @Enumerated(EnumType.STRING)
  @Column(name = "game_type", nullable = false, length = 7)
  private GameType type;

  @Column(name = "orgagame", nullable = false)
  private boolean isOrgagame = false;

  @OneToMany(mappedBy = "game")
  @ToString.Exclude
  private Set<TeamPerf> teamPerformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "game")
  @ToString.Exclude
  private Set<Selection> selections = new LinkedHashSet<>();

}