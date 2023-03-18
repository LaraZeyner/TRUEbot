package de.zahrie.trues.api.riot.matchhistory.game;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.riot.matchhistory.selection.Selection;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
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
    indexes = {@Index(name = "game_idx_orgagame", columnList = "orgagame")})
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
  @Column(name = "game_type", nullable = false, length = 12)
  private GameType type;

  @Column(name = "orgagame", nullable = false)
  private boolean isOrgagame = false;

  @OneToMany(mappedBy = "game")
  @ToString.Exclude
  private Set<TeamPerf> teamPerformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "game")
  @ToString.Exclude
  private Set<Selection> selections = new LinkedHashSet<>();

  public Game(String id, Time start, int durationInSeconds, GameType type) {
    this(id, start, durationInSeconds, type, false);
  }

  public Game(String id, Time start, int durationInSeconds, GameType type, boolean isOrgagame) {
    this.id = id;
    this.start = start;
    this.durationInSeconds = durationInSeconds;
    this.type = type;
    this.isOrgagame = isOrgagame;
  }

  public boolean hasSelections() {
    return !selections.isEmpty();
  }
}
