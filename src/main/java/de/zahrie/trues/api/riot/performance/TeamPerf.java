package de.zahrie.trues.api.riot.performance;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.riot.KDA;
import de.zahrie.trues.api.riot.Side;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
@Setter
@Table("team_perf")
@ExtensionMethod(SQLUtils.class)
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
  private Team team; // team

  public void setTeam(Team team) {
    this.team = team;
    new Query<>(TeamPerf.class).col("team", team).update(id);
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Performance> getPerformances() {
    return new Query<>(Performance.class).where("t_perf", this).entityList();
  }

  public static TeamPerf get(List<Object> objects) {
    return new TeamPerf(
        (int) objects.get(0),
        new Query<>(Game.class).entity(objects.get(1)),
        new SQLEnum<>(Side.class).of(objects.get(2)),
        (boolean) objects.get(3),
        new KDA(objects.get(4).shortValue(), objects.get(5).shortValue(), objects.get(6).shortValue()),
        (Integer) objects.get(7),
        (Integer) objects.get(8),
        (Integer) objects.get(9),
        (Integer) objects.get(10),
        new Objectives((int) objects.get(11), (int) objects.get(12), (int) objects.get(13), (int) objects.get(14), (int) objects.get(15)),
        new Query<>(Team.class).entity(objects.get(16)));
  }

  @Override
  public TeamPerf create() {
    return new Query<>(TeamPerf.class)
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

  public Team getOpposingTeam() {
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
