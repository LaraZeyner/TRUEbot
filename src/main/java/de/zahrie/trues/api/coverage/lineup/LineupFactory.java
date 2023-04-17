package de.zahrie.trues.api.coverage.lineup;

import java.util.List;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;

public class LineupFactory {
  public static Lineup determineLineup(Participator participator, Player player) {
    return QueryBuilder.hql(Lineup.class, "FROM Lineup WHERE participator = " + participator.getId() + " AND player = " + player.getId()).single();
  }

  public static Lineup determineLineup(Participator participator, Lane lane) {
    return QueryBuilder.hql(Lineup.class, "FROM Lineup WHERE participator = " + participator.getId() + " AND lane = " + lane).single();
  }

  /**
   * Bestimme das wahrscheinlichste Lineup für ein Team
   */
  public static List<Lineup> determineLineup(Team team) {
    final var participator = new Participator(false, team);
    return new MatchLineup(participator).getLineup();
  }
}
