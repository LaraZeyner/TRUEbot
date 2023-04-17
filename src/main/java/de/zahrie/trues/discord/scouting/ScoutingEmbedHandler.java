package de.zahrie.trues.discord.scouting;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.coverage.lineup.LineupCreator;
import de.zahrie.trues.api.coverage.lineup.LineupFinder;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.PlayerAnalyzer;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.TeamAnalyzer;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record ScoutingEmbedHandler(Participator participator, ScoutingGameType gameType, int days, int page) {

  public List<MessageEmbed.Field> get(Scouting.ScoutingType type) {
    return switch (type) {
      case CHAMPIONS -> getChampions(participator, gameType, days);
      case HISTORY -> getHistory(participator, gameType, days, page);
      case LINEUP -> getLineup(participator, gameType, days);
      case MATCHUPS -> getMatchups(participator, gameType, days);
      case OVERVIEW -> getOverview(participator, gameType, days);
      case PLAYER_HISTORY -> List.of();
      case SCHEDULE -> getSchedule(participator);
    };
  }

  public List<MessageEmbed.Field> getOverview(Participator participator, ScoutingGameType gameType, int days) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    for (Lineup lineup : LineupFinder.getLineup(participator, gameType, days)) {
      final Player player = lineup.getPlayer();
      final var analyzer = new PlayerAnalyzer(player, gameType, player.getTeam(), days);
      fields.addAll(analyzer.analyzePicks(lineup.getLane()));
    }
    return fields;
  }

  public List<MessageEmbed.Field> getMatchups(Participator participator, ScoutingGameType gameType, int days) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    for (Lineup lineup : LineupFinder.getLineup(participator, gameType, days)) {
      final Player player = lineup.getPlayer();
      final var analyzer = new PlayerAnalyzer(player, gameType, player.getTeam(), days);
      fields.addAll(analyzer.analyzeMatchups(lineup.getLane()));
    }
    return fields;
  }

  public List<MessageEmbed.Field> getHistory(Participator participator, ScoutingGameType gameType, int days, int page) {
    final Team team = participator.getTeam();
    final var analyzer = new TeamAnalyzer(team, gameType, days);
    return analyzer.analyzeHistory(page);
  }

  public List<MessageEmbed.Field> getChampions(Participator participator, ScoutingGameType gameType, int days) {
    final Team team = participator.getTeam();
    final var analyzer = new TeamAnalyzer(team, gameType, days);
    return analyzer.analyzeChampions();
  }

  public List<MessageEmbed.Field> getSchedule(Participator participator) {
    final Team team = participator.getTeam();
    final var analyzer = new TeamAnalyzer(team, gameType, days);
    return analyzer.analyzeSchedule();
  }

  public List<MessageEmbed.Field> getLineup(Participator participator, ScoutingGameType gameType, int days) {
    List<MessageEmbed.Field> fields = new ArrayList<>();
    final LineupCreator lineupCreator = new LineupCreator(participator, gameType, days);
    for (Lane lane : Lane.values()) {
      if (lane.equals(Lane.UNKNOWN)) continue;
      fields = new EmbedFieldBuilder<>(fields, lineupCreator.getPlayersOnLane(lane))
          .add(lane.getDisplayName(), laneGames -> laneGames.player().getSummonerName())
          .add("Rank (Solo/Duo)", laneGames -> laneGames.player().getLastRank().toString())
          .add("Lanegames", laneGames -> String.valueOf(laneGames.amount()))
          .build();
    }
    return fields;
  }
}
