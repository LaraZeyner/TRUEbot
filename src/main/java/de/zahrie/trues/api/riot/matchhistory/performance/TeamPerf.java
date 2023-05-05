package de.zahrie.trues.api.riot.matchhistory.performance;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.Side;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
@Setter
@Table("team_perf")
public class TeamPerf implements Entity<TeamPerf> {
  @Serial
  private static final long serialVersionUID = 4138620147627390023L;

  private int id; // team_perf_id
  private final Game game; // game
  private final Side side; // first
  private final boolean win; // win
  private final KDA kda;
  private final Integer totalGold; // total_gold
  private final Integer totalDamage; // total_damage
  private final Integer totalVision; // total_vision
  private final Integer totalCreeps; // total_creeps
  private final Objectives objectives; // turrets, drakes, inhibs, heralds, barons;
  private TeamBase team; // team

  public void setTeam(TeamBase team) {
    this.team = team;
    new Query<TeamPerf>().col("team", team).update(id);
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Performance> getPerformances() {
    return new Query<Performance>().where("t_perf", this).entityList();
  }

  public static TeamPerf get(Object[] objects) {
    return new TeamPerf(
        (int) objects[0],
        new Query<Game>().entity( objects[1]),
        new SQLEnum<Side>().of(objects[2]),
        (boolean) objects[3],
        new KDA((short) objects[4], (short) objects[5], (short) objects[6]),
        (Integer) objects[7],
        (Integer) objects[8],
        (Integer) objects[9],
        (Integer) objects[10],
        new Objectives((int) objects[11], (int) objects[12], (int) objects[13], (int) objects[14], (int) objects[15]),
        new Query<Team>().entity( objects[16]));
  }

  @Override
  public TeamPerf create() {
    return new Query<TeamPerf>()
        .key("game", game).key("first", side).key("win", win).key("kills", kda.kills()).key("deaths", kda.deaths())
        .key("assists", kda.assists()).key("total_gold", totalGold).key("total_damage", totalDamage).key("total_vision", totalVision)
        .key("total_creeps", totalCreeps).key("turrets", objectives.turrets()).key("drakes", objectives.drakes())
        .key("inhibs", objectives.inhibs()).key("heralds", objectives.heralds()).key("barons", objectives.barons())
        .col("team", team)
        .insert(this);
  }

  public TeamPerf getOpponent() {
    return game.getTeamPerformances().stream().filter(teamPerf -> !teamPerf.equals(this)).findFirst().orElse(null);
  }

  public TeamBase getOpposingTeam() {
    return Util.avoidNull(getOpponent(), null, TeamPerf::getTeam);
  }

  public String getWinString() {
    return win ? "Gewonnen" : "Verloren";
  }

  public record Objectives(byte turrets, byte drakes, byte inhibs, byte heralds, byte barons) {
    public Objectives(int turrets, int drakes, int inhibs, int heralds, int barons) {
      this((byte) turrets, (byte) drakes, (byte) inhibs, (byte) heralds, (byte) barons);
    }
  }
}
