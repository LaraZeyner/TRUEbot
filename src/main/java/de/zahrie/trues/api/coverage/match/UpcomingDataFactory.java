package de.zahrie.trues.api.coverage.match;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.database.Database;

public final class UpcomingDataFactory {
  private static UpcomingDataFactory instance;

  public static UpcomingDataFactory getInstance() {
    return instance;
  }

  public static void refresh() {
    instance = new UpcomingDataFactory();
  }


  private final List<Match> nextMatches;

  private UpcomingDataFactory() {
    this.nextMatches = Database.Find.findList(Match.class, "nextMatches").stream()
        .filter(match -> match.getStart().before(Time.of().plus(Calendar.HOUR, 3))).toList();
  }

  public Set<Team> getTeams() {
    return nextMatches.stream().flatMap(match -> match.getParticipators().stream()).map(Participator::getTeam).collect(Collectors.toSet());
  }

  public List<Match> getMatches() {
    return nextMatches;
  }
}
