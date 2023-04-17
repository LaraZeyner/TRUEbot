package de.zahrie.trues.api.riot.matchhistory.game;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.riot.matchhistory.selection.Selection;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.util.Util;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "game", indexes = @Index(name = "game_idx_orgagame", columnList = "orgagame"))
public class Game implements Serializable {
  @Serial
  private static final long serialVersionUID = -2880626253889374458L;


  @Id
  @Column(name = "game_id", nullable = false, length = 16, unique = true)
  private String id;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime start;

  @Column(name = "duration", columnDefinition = "SMALLINT UNSIGNED not null")
  private int durationInSeconds;

  @Enumerated
  @Column(name = "game_type", nullable = false)
  private GameType type;

  @Column(name = "orgagame", nullable = false)
  private boolean isOrgagame = false;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
  private Set<TeamPerf> teamPerformances = new LinkedHashSet<>();

  public void addTeamPerformance(TeamPerf teamPerf) {
    teamPerf.setGame(this);
    teamPerformances.add(teamPerf);
    Database.update(this);
    Database.update(teamPerf);
  }

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
  private Set<Selection> selections = new LinkedHashSet<>();

  public void addSelection(Selection selection) {
    selection.setGame(this);
    selections.add(selection);
    Database.update(selection);
    Database.update(this);
  }

  public Game(String id, LocalDateTime start, int durationInSeconds, GameType type) {
    this(id, start, durationInSeconds, type, false);
  }

  public Game(String id, LocalDateTime start, int durationInSeconds, GameType type, boolean isOrgagame) {
    this.id = id;
    this.start = start;
    this.durationInSeconds = durationInSeconds;
    this.type = type;
    this.isOrgagame = isOrgagame;
  }

  public boolean hasSelections() {
    return !selections.isEmpty();
  }

  public String getDuration() {
    return Util.formatDuration(durationInSeconds);
  }
}
