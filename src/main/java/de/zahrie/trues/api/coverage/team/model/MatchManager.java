package de.zahrie.trues.api.coverage.team.model;

import java.util.List;

import de.zahrie.trues.api.coverage.match.model.ATournament;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.model.CalibrationStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import org.jetbrains.annotations.Nullable;

public record MatchManager(TeamBase team) {
  public List<Match> getUpcomingMatches() {
    return new Query<Participator>().join(new JoinQuery<Participator, Match>(JoinQuery.JoinType.LEFT, "match"))
        .where("team", team).and("_match.result", "-:-")
        .ascending("_match.coverage_start").convertList(Match.class);
  }

  @Nullable
  public Match getNextMatch(boolean avoidCalibration) {
    final List<Match> nextMatches = new Query<Participator>().join(new JoinQuery<Participator, Match>("coverage", "_match"))
        .where("team", team).and("_match.result", "-:-")
        .ascending("_match.coverage_start")
        .include(new Query<Participator>().join(new JoinQuery<Participator, Match>("coverage", "_match"))
            .where("team", team).and(Condition.Comparer.NOT_EQUAL, "_match.result", "-:-")
            .descending("_match.coverage_start")
        ).convertList(Match.class);
    if (avoidCalibration) {
      return nextMatches.stream().filter(match -> !(match instanceof ATournament tM && tM.getLeague().getStage() instanceof CalibrationStage)).findFirst().orElse(null);
    }
    return nextMatches.stream().findFirst().orElse(null);
  }

  public List<Match> getMatchesOf(Season season) {
    return new Query<Participator>().join(new JoinQuery<Participator, Match>())
        .join(new JoinQuery<Match, Playday>("matchday"))
        .join(new JoinQuery<Playday, Stage>("_playday.stage"))
        .where("team", team).and("_stage.season", season)
        .ascending("_match.coverage_start").convertList(Match.class);
  }

  public List<Match> getMatchesOf(Stage stage) {
    return new Query<Participator>().join(new JoinQuery<Participator, Match>())
        .join(new JoinQuery<Match, Playday>("matchday"))
        .where("team", team).and("_playday.stage", stage)
        .ascending("_match.coverage_start").convertList(Match.class);
  }
}
