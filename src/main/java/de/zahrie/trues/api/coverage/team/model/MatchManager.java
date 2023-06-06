package de.zahrie.trues.api.coverage.team.model;

import java.util.List;

import de.zahrie.trues.api.coverage.match.model.ATournament;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.model.CalibrationStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public record MatchManager(Team team) {
  public List<Match> getUpcomingMatches() {
    return new Query<>(Participator.class)
        .join(new JoinQuery<>(Participator.class, Match.class, JoinQuery.JoinType.LEFT).col("coverage"))
        .where("team", team).and("_match.result", "-:-")
        .ascending("_match.coverage_start").convertList(Match.class);
  }

  @Nullable
  public Match getNextMatch(boolean avoidCalibration) {
    final List<Match> nextMatches = getNextMatches(avoidCalibration);
    return nextMatches.isEmpty() ? null : nextMatches.get(0);
  }

  @NonNull
  public List<Match> getNextMatches(boolean avoidCalibration) {
    final List<Match> nextMatches = new Query<>(Participator.class)
        .join(new JoinQuery<>(Participator.class, Match.class).col("coverage"))
        .where("team", team).and("_match.result", "-:-")
        .ascending("_match.coverage_start")
        .include(new Query<>(Participator.class).get("_match.*", Object[].class)
            .join(new JoinQuery<>(Participator.class, Match.class).col("coverage"))
            .where("team", team).and(Condition.Comparer.NOT_EQUAL, "_match.result", "-:-")
            .descending("_match.coverage_start")
        ).convertList(Match.class);
    if (avoidCalibration) {
      return nextMatches.stream().filter(match -> !(match instanceof ATournament tM && tM.getLeague().getStage() instanceof CalibrationStage)).toList();
    }
    return nextMatches;
  }

  public List<Match> getMatchesOf(Season season) {
    return new Query<>(Match.class, "SELECT _match.* FROM coverage_team as _participator " +
        "INNER JOIN coverage as _match ON _participator.coverage = _match.coverage_id " +
        "INNER JOIN coverage_playday as _playday ON _match.matchday = _playday.coverage_playday_id " +
        "INNER JOIN coverage_stage as _stage ON _playday.stage = _stage.coverage_stage_id " +
        "WHERE (team = ? and _stage.season = ?) ORDER BY _match.coverage_start LIMIT 1000")
        .entityList(List.of(team, season));
  }

  public List<Match> getMatchesOf(Stage stage) {
    return new Query<>(Participator.class)
        .join(new JoinQuery<>(Participator.class, Match.class).col("coverage"))
        .join(new JoinQuery<>(Match.class, Playday.class).col("matchday"))
        .where("team", team).and("_playday.stage", stage)
        .ascending("_match.coverage_start").convertList(Match.class);
  }
}
