package de.zahrie.trues.api.scouting;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLGroup;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import de.zahrie.trues.api.riot.matchhistory.game.Selection;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.TeamPerf;

public class TeamScoutingQuery<T> extends AbstractScoutingQuery<T> {
  protected final TeamBase team;

  TeamScoutingQuery(Class<T> clazz, ScoutingGameType gameType, int days, TeamBase team) {
    super(clazz, gameType, days);
    this.team = team;
  }

  @Override
  public Query<Performance> performance() {
    return gameTypeString(new Query<Performance>().distinct("_teamperf.game", Integer.class)
        .join(new JoinQuery<Performance, TeamPerf>("t_perf")).join(new JoinQuery<TeamPerf, Game>())
        .join(new JoinQuery<Performance, Player>())
        .where("_player.team", team).and(Condition.Comparer.GREATER_EQUAL, "_game.start_time", start));
  }

  @Override
  public Query<Selection> selection() {
    final Query<Performance> performanceQuery = gameTypeString(new Query<Performance>().distinct("_teamperf.game", Integer.class)
        .join(new JoinQuery<Performance, TeamPerf>("t_perf")).join(new JoinQuery<TeamPerf, Game>())
        .join(new JoinQuery<Performance, Player>())
        .where("_player.team", team).and(Condition.Comparer.GREATER_EQUAL, "_game.start_time", start));

    return new Query<Selection>().where(Condition.inSubquery("game", performanceQuery));
  }

  @Override
  protected Query<Performance> gameTypeString(Query<Performance> query) {
    switch (gameType) {
      case PRM_ONLY -> query.where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.CUSTOM);
      case PRM_CLASH -> query.where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.CLASH);
      case TEAM_GAMES -> {
        if (team == null) query.where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.CLASH);
        else {
          query.join(new JoinQuery<Performance, Player>())
              .where("_player.team", team)
              .and(Condition.inSubquery("t_perf", new Query<Performance>().distinct("t_perf", Integer.class)
                      .join(new JoinQuery<Performance, TeamPerf>("t_perf")).join(new JoinQuery<TeamPerf, Game>())
                      .join(new JoinQuery<Performance, Player>())
                      .where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.CLASH).and("_player.team", team)
                      .with(new Query<Performance>().distinct("t_perf", Integer.class)
                          .join(new JoinQuery<Performance, TeamPerf>("t_perf")).join(new JoinQuery<TeamPerf, Game>())
                          .join(new JoinQuery<Performance, Player>())
                          .where(Condition.Comparer.SMALLER_EQUAL, "_game.game_type", GameType.RANKED_FLEX).and("_player.team", team)
                          .groupBy(new SQLGroup("t_perf, _player.team").having("count(performance_id) > 2"))
                      )
                  )
              );
        }
      }
    }
    return query;
  }
}

