package de.zahrie.trues.api.coverage.lineup;

import java.util.List;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;

public class LineupFactory {
  public static Lineup determineLineup(Participator participator, PlayerBase player) {
    return new Query<Lineup>().where("coverage_team", participator).and("player", player).entity();
  }

  public static Lineup determineLineup(Participator participator, Lane lane) {
    return new Query<Lineup>().where("coverage_team", participator).and("lineup_id", lane).entity();
  }

  /**
   * Bestimme das wahrscheinlichste Lineup für ein Team
   */
  public static List<Lineup> determineLineup(Team team) {
    final var participator = new Participator(null, false, team);
    return new MatchLineup(participator).getLineup();
  }
}
