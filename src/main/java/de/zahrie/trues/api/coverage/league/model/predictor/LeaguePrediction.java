package de.zahrie.trues.api.coverage.league.model.predictor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.MatchResult;
import de.zahrie.trues.api.coverage.match.model.AMatch;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.collections.SortedList;
import lombok.Getter;

@Getter
public final class LeaguePrediction {
  public static LeaguePrediction of(League league) {
    final LeaguePrediction prediction = new LeaguePrediction(
        league.getLeagueTeams().stream().map(LeagueTeam::getTeam).toList(),
        SortedList.of(league.getMatches().stream().filter(AMatch::isRunning)), Prediction.generate(league)
    );
    return prediction.calculatePredictions();
  }

  private final List<Team> teams;
  private final List<Match> matches;
  private final Prediction teamPoints;
  private final Map<Team, Map<LeagueResult, Integer>> predictions;
  private final List<MatchResult> currentResults;
  private long started;
  private long index;

  public LeaguePrediction(List<Team> teams, List<Match> matches, Prediction teamPoints) {
    this.teams = teams;
    this.matches = matches;
    this.teamPoints = teamPoints;
    this.predictions = new HashMap<>();
    this.currentResults = SortedList.of();
  }

  public LeaguePrediction calculatePredictions() {
    if (matches.isEmpty()) {
      for (final Team team : teams) {
        add(team, teamPoints.getResultOfTeam(team));
      }
      return this;
    }

    this.started = System.currentTimeMillis();
    nested(matches.size(), 0);
    return this;
  }

  private void add(Team team, LeagueResult leagueResult) {
    Map<LeagueResult, Integer> map = predictions.get(team);
    if (map == null) map = new HashMap<>();
    map.merge(leagueResult, 1, Integer::sum);
    predictions.put(team, map);
  }

  private void nested(int max, int current) {
    if (current == max) {
      final Prediction prediction = new Prediction(teamPoints.teamPoints()).add(currentResults);
      for (final Team team : teams) {
        add(team, prediction.getResultOfTeam(team));
      }
      currentResults.clear();
      this.index = index + 1;
      if (index % 1_000_000 == 0) {
        System.out.println(index / 1_000_000 + " Mio : " + (System.currentTimeMillis() - started)  / 1000.);
      }
      return;
    }

    final Match currentMatch = matches.get(current);
    for (MatchResult outcome : currentMatch.getResult().getPossibleOutcomes()) {
      currentResults.add(outcome);
      nested(max, current + 1);
    }
  }
}
