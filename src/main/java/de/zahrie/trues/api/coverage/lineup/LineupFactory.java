package de.zahrie.trues.api.coverage.lineup;

import java.util.List;

import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.models.riot.Lane;
import de.zahrie.trues.util.database.Database;

/**
 * Created by Lara on 19.02.2023 for TRUEbot
 */
public class LineupFactory {

  public static Lineup determineLineup(Participator participator, Player player) {
    return Database.Find.find(Lineup.class, new String[]{"participator", "player"}, new Object[]{participator, player}, "fromParticipatorAndPlayer");
  }

  public static Lineup determineLineup(Participator participator, Lane lane) {
    return Database.Find.find(Lineup.class, new String[]{"participator", "lane"}, new Object[]{participator, lane}, "fromParticipatorAndLane");
  }

  public static List<Lineup> determineLineup(Team team) {
    final var participator = new Participator(false, team);
    return new MatchLineup(participator).getLineup();
  }

}
