package de.zahrie.trues.api.coverage.match;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.collections.SortedList;

public final class UpcomingDataFactory {
  private static UpcomingDataFactory instance;

  public static UpcomingDataFactory getInstance() {
    if (instance == null) refresh();
    return instance;
  }

  public static void refresh() {
    instance = new UpcomingDataFactory();
  }

  private final List<Match> nextMatches;

  private UpcomingDataFactory() {
    this.nextMatches = new Query<>(Match.class).where(Condition.Comparer.SMALLER_EQUAL, "coverage_start", LocalDateTime.now().plusHours(3))
        .and("result", "-:-").ascending("coverage_start").entityList();
  }

  public List<Team> getTeams() {
    return new SortedList<>(nextMatches.stream().flatMap(match -> Arrays.stream(match.getParticipators())).map(Participator::getTeam).collect(Collectors.toSet()));
  }

  public List<Match> getMatches() {
    return nextMatches;
  }
}
