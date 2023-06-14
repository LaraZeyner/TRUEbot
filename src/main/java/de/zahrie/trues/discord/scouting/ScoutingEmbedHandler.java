package de.zahrie.trues.discord.scouting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.coverage.participator.TeamLineup;
import de.zahrie.trues.api.coverage.participator.model.Lineup;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.api.riot.performance.Lane;
import de.zahrie.trues.api.scouting.PlayerAnalyzer;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.api.scouting.TeamAnalyzer;
import de.zahrie.trues.util.Util;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record ScoutingEmbedHandler(Team team, ScoutingGameType gameType, int days, int page, List<Lineup> lineups, Map<Player, PlayerAnalyzer> analyzerMap) {

  public ScoutingEmbedHandler(Team team, List<Lineup> lineups, ScoutingGameType gameType, int days, int page) {
    this(team, gameType, days, page, lineups, new HashMap<>());
  }

  public List<MessageEmbed.Field> get(ScoutingType type, Participator participator) {
    return switch (type) {
      case CHAMPIONS -> getChampions();
      case HISTORY -> getHistory();
      case LINEUP -> getLineup(participator);
      case MATCHUPS -> getMatchups();
      case OVERVIEW -> getOverview();
      case PLAYER_HISTORY -> List.of(); //TODO (Abgie) 17.05.2023:
      case SCHEDULE -> getSchedule();
    };
  }

  public List<MessageEmbed.Field> getOverview() {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    for (Lineup lineup : lineups) {
      if (lineup.getPlayer() == null) fields.add(new MessageEmbed.Field("kein Spieler gefunden", "no Data", false));
      else {
        final PlayerAnalyzer playerAnalyzer = analyzerMap.computeIfAbsent(lineup.getPlayer(), player -> player.analyze(gameType, days));
        fields.addAll(playerAnalyzer.analyzePicks(lineup.getLane()));
      }
    }
    return fields;
  }

  public List<MessageEmbed.Field> getMatchups() {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    for (Lineup lineup : lineups) {
      if (lineup.getPlayer() == null) fields.add(new MessageEmbed.Field("kein Spieler gefunden", "no Data", false));
      else {
        final PlayerAnalyzer playerAnalyzer = analyzerMap.computeIfAbsent(lineup.getPlayer(), player -> player.analyze(gameType, days));
        fields.addAll(playerAnalyzer.analyzeMatchups(lineup.getLane()));
      }
    }
    return fields;
  }

  public List<MessageEmbed.Field> getHistory() {
    return team.analyze(gameType, days).analyzeHistory(page);
  }

  public List<MessageEmbed.Field> getChampions() {
    final TeamAnalyzer analyze = team.analyze(gameType, days);
    return new EmbedFieldBuilder<>(analyze.handleChampions())
        .num("Champion", championData -> championData.champion().getName())
        .add("Picks", TeamAnalyzer.ChampionData::getPicksString)
        .add("KDA", TeamAnalyzer.ChampionData::getKDAString)
        .build();
  }

  public List<MessageEmbed.Field> getSchedule() {
    final TeamAnalyzer analyze = team.analyze(gameType, days);
    return new EmbedFieldBuilder<>(analyze.getMatches())
        .add("Spielzeit", match -> TimeFormat.DISCORD.of(match.getStart()))
        .add("Gegner", match -> Util.avoidNull(match.getOpponentOf(team), "keine Daten", Team::getName))
        .add("Ergebnis", match -> match.getResult().toString()).build();
  }

  public List<MessageEmbed.Field> getLineup(Participator participator) {
    List<MessageEmbed.Field> fields = new ArrayList<>();
    if (participator == null) participator = new Participator(null, true, team);
    final TeamLineup lineupCreator = participator.getTeamLineup(gameType, days);
    for (Lane lane : Lane.values()) {
      if (lane.equals(Lane.UNKNOWN)) continue;
      fields = new EmbedFieldBuilder<>(fields, lineupCreator.getPlayersOnLane(lane))
          .add(lane.getDisplayName(), laneGames -> laneGames.player().getSummonerName())
          .add("Rank (Solo/Duo)", laneGames -> laneGames.player().getRanks().getCurrent().toString())
          .add("Lanegames", laneGames -> String.valueOf(laneGames.amount()))
          .build();
    }
    return fields;
  }
}
