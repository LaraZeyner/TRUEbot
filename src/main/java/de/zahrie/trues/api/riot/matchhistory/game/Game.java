package de.zahrie.trues.api.riot.matchhistory.game;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.matchhistory.performance.TeamPerf;
import de.zahrie.trues.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
@Setter
@Table("game")
public class Game implements Entity<Game>, Comparable<Game> {
  @Serial
  private static final long serialVersionUID = -1636645398510925983L;

  private int id; // game_id
  private final String gameId; // game_index
  private final LocalDateTime start; // start_time
  private final int durationInSeconds; // duration
  private final GameType type; // game_type
  private boolean orgaGame = false; // orgagame

  public List<TeamPerf> getTeamPerformances() {
    return new Query<TeamPerf>().where("game", this).entityList();
  }

  public List<Selection> getSelections() {
    return new Query<Selection>().where("game", this).entityList();
  }

  public static Game get(Object[] objects) {
    return new Game(
        (int) objects[0],
        (String) objects[1],
        (LocalDateTime) objects[2],
        (int) objects[3],
        new SQLEnum<GameType>().of(objects[4]),
        (boolean) objects[5]
    );
  }

  @Override
  public Game create() {
    return new Query<Game>().key("game_index", gameId)
        .col("start_time", start).col("duration", durationInSeconds).col("game_type", type).col("orgagame", orgaGame)
        .insert(this);
  }

  public void setOrgaGame(boolean orgaGame) {
    if (orgaGame != this.orgaGame) new Query<Game>().col("orgagame", orgaGame).update(id);
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
