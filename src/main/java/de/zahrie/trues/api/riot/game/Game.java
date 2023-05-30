package de.zahrie.trues.api.riot.game;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.performance.TeamPerf;
import de.zahrie.trues.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
@Table("game")
@ExtensionMethod(SQLUtils.class)
public class Game implements Entity<Game>, Comparable<Game> {
  @Serial
  private static final long serialVersionUID = -1636645398510925983L;

  @Setter
  private int id; // game_id
  private final String gameId; // game_index
  private final LocalDateTime start; // start_time
  private final int durationInSeconds; // duration
  private final GameType type; // game_type
  private boolean orgaGame = false; // orgagame

  private List<Selection> selections;

  public List<Selection> getSelections() {
    if (selections == null) this.selections = new Query<>(Selection.class).where("game", this).entityList();
    return selections;
  }

  public boolean addSelection(Selection selection) {
    return getSelections().add(selection);
  }
  private List<TeamPerf> teamPerformances;

  public List<TeamPerf> getTeamPerformances() {
    if (teamPerformances == null) this.teamPerformances = new Query<>(TeamPerf.class).where("game", this).entityList();
    return teamPerformances;
  }

  public boolean addTeamPerformance(TeamPerf teamPerf) {
    return getTeamPerformances().add(teamPerf);
  }

  private Game(int id, String gameId, LocalDateTime start, int durationInSeconds, GameType type, boolean orgaGame) {
    this.id = id;
    this.gameId = gameId;
    this.start = start;
    this.durationInSeconds = durationInSeconds;
    this.type = type;
    this.orgaGame = orgaGame;
  }

  public static Game get(List<Object> objects) {
    return new Game(
        objects.get(0).intValue(),
        (String) objects.get(1),
        (LocalDateTime) objects.get(2),
        (int) objects.get(3),
        new SQLEnum<>(GameType.class).of(objects.get(4)),
        (boolean) objects.get(5)
    );
  }

  @Override
  public Game create() {
    return new Query<>(Game.class).key("game_index", gameId)
        .col("start_time", start).col("duration", durationInSeconds).col("game_type", type).col("orgagame", orgaGame)
        .insert(this);
  }

  public void setOrgaGame(boolean orgaGame) {
    if (orgaGame != this.orgaGame) new Query<>(Game.class).col("orgagame", orgaGame).update(id);
    this.orgaGame = orgaGame;
  }

  public boolean hasSelections() {
    return !getSelections().isEmpty();
  }

  public String getDuration() {
    return Util.formatDuration(durationInSeconds);
  }

  @Override
  public int compareTo(@NotNull Game o) {
    return start.compareTo(o.getStart());
  }
}
