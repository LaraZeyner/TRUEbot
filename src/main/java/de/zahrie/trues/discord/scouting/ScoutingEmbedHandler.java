package de.zahrie.trues.discord.scouting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.participator.TeamLineup;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.api.riot.performance.Lane;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.api.scouting.TeamAnalyzer;
import de.zahrie.trues.util.Util;
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
    return participator.getTeamLineup(gameType, days).getLineup().stream()
        .flatMap(lineup -> lineup.getPlayer().analyze(gameType, days).analyzePicks(lineup.getLane()).stream())
        .collect(Collectors.toList());
  }

  public List<MessageEmbed.Field> getMatchups(Participator participator, ScoutingGameType gameType, int days) {
    return participator.getTeamLineup(gameType, days).getLineup().stream()
        .flatMap(lineup -> lineup.getPlayer().analyze(gameType, days).analyzeMatchups(lineup.getLane()).stream())
        .collect(Collectors.toList());
  }

  public List<MessageEmbed.Field> getHistory(Participator participator, ScoutingGameType gameType, int days, int page) {
    final Team team = participator.getTeam();
    return team.analyze(gameType, days).analyzeHistory(page);
  }

  public List<MessageEmbed.Field> getChampions(Participator participator, ScoutingGameType gameType, int days) {
    final Team team = participator.getTeam();
    final TeamAnalyzer analyze = team.analyze(gameType, days);
    return new EmbedFieldBuilder<>(analyze.handleChampions())
        .num("Champion", championData -> championData.champion().getName())
        .add("Picks", TeamAnalyzer.ChampionData::getPicksString)
        .add("KDA", TeamAnalyzer.ChampionData::getKDAString)
        .build();
  }

  public List<MessageEmbed.Field> getSchedule(Participator participator) {
    final Team team = participator.getTeam();
    final TeamAnalyzer analyze = team.analyze(gameType, days);
    return new EmbedFieldBuilder<>(analyze.getMatches())
        .add("Spielzeit", match -> TimeFormat.DISCORD.of(match.getStart()))
        .add("Gegner", match -> Util.avoidNull(match.getOpponentOf(team), "keine Daten", Team::getName))
        .add("Ergebnis", match -> match.getResult().toString()).build();
  }

  public List<MessageEmbed.Field> getLineup(Participator participator, ScoutingGameType gameType, int days) {
    List<MessageEmbed.Field> fields = new ArrayList<>();
    final TeamLineup lineupCreator = participator.getTeamLineup(gameType, days);
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
