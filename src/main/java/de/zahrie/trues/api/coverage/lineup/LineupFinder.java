package de.zahrie.trues.api.coverage.lineup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.discord.scouting.Scouting;
import de.zahrie.trues.discord.scouting.ScoutingManager;

public final class LineupFinder {
  private static final Map<Participator, List<Lineup>> lineups = new HashMap<>();

  static {
    ScoutingManager.getScoutings().values().stream().map(Scouting::participator).forEach(LineupFinder::update);
  }

  public static List<Lineup> getLineup(Participator participator) {
    return getLineup(participator, ScoutingGameType.TEAM_GAMES, 180);
  }

  public static List<Lineup> getLineup(Participator participator, ScoutingGameType gameType, int days) {
    if (gameType.equals(ScoutingGameType.TEAM_GAMES) && days == 180) {
      final List<Lineup> lineup = lineups.getOrDefault(participator, null);
      return lineup == null ? update(participator) : lineup;
    }
    return new LineupCreator(participator, gameType, days).handleLineup();
  }

  public static List<Lineup> update(Participator participator) {
    final List<Lineup> lineup = new LineupCreator(participator).handleLineup();
    lineups.put(participator, lineup);
    return lineup;
  }
}
