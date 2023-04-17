package de.zahrie.trues.api.coverage.team.model;

import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.TournamentMatch;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.stage.model.CalibrationStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.database.QueryBuilder;
import org.jetbrains.annotations.Nullable;

public record MatchManager(Team team) {
  public List<Match> getUpcomingMatches() {
    return QueryBuilder.hql(Match.class, "SELECT coverage FROM Participator WHERE team = :team and coverage.result = '-:-' ORDER BY coverage.start").addParameter("team", team).list();
  }

  @Nullable
  public Match getNextMatch(boolean avoidCalibration) {
    final List<Match> nexts = QueryBuilder.hql(Match.class,
        "SELECT coverage FROM Participator WHERE team = :team AND coverage.result = '-:-' ORDER BY coverage.start").addParameter("team", team).list();
    nexts.addAll(QueryBuilder.hql(Match.class,
        "SELECT coverage FROM Participator WHERE team = :team AND coverage.result <> '-:-' ORDER BY coverage.start desc").addParameter("team", team).list());
    if (avoidCalibration) {
      return nexts.stream().filter(match -> !(match instanceof TournamentMatch tM && tM.getLeague().getStage() instanceof CalibrationStage)).findFirst().orElse(null);
    }
    return nexts.stream().findFirst().orElse(null);

  }

  public List<Match> getNextMatches() {
    return QueryBuilder.hql(Match.class,
        "SELECT coverage FROM Participator WHERE team = " + team.getId() + " AND coverage.result = '-:-' ORDER BY coverage").list();
  }

  public List<Match> getMatchesOf(Season season) {
    return QueryBuilder.hql(Match.class, "SELECT coverage FROM Participator WHERE team = :team and coverage.playday.stage.season = :season ORDER BY coverage.start").addParameters(Map.of("team", team, "season", season)).list();
  }

  public List<Match> getMatchesOf(Stage stage) {
    return QueryBuilder.hql(Match.class, "SELECT coverage FROM Participator WHERE team = :team and coverage.playday.stage = :stage ORDER BY coverage.start").addParameters(Map.of("team", team, "stage", stage)).list();
  }
}
