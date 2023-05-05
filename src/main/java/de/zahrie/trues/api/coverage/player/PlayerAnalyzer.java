package de.zahrie.trues.api.coverage.player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLGroup;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.matchhistory.game.Selection;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.PlayerMatchHistoryPerformanceDTO;
import de.zahrie.trues.api.riot.matchhistory.performance.TeamPerf;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PlayerAnalyzer(PlayerBase player, ScoutingGameType type, TeamBase team, int days) {
  public List<MessageEmbed.Field> analyzePicks(Lane lane) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    fields.add(getPlayerHeadField(lane));
    final var data = new EmbedFieldBuilder<>(handlePicks())
        .add("Champion", PlayerAnalyzer.PlayerAnalyzerData::getChampionString)
        .add("Competitive", PlayerAnalyzer.PlayerAnalyzerData::getCompetitiveString)
        .add("Alle Games", PlayerAnalyzer.PlayerAnalyzerData::getMatchmadeString);
    fields.addAll(data.build());
    return fields;
  }

  public List<MessageEmbed.Field> analyzeGamesWith(@Nullable Champion champion, @Nullable Lane lane) {
    final List<List<String>> entries = PlayerMatchHistoryPerformanceDTO.get(player, type, lane, champion).entityList(25).stream().map(performance -> new PlayerMatchHistoryPerformanceDTO(performance).getData()).toList();
    final var data = new EmbedFieldBuilder<>(entries)
        .add("Zeitpunkt", entry -> entry.get(0))
        .add("Matchup", entry -> entry.get(1))
        .add("KDA", entry -> entry.get(2));
    return new ArrayList<>(data.build());
  }

  public List<MessageEmbed.Field> analyzeMatchups(Lane lane) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();
    fields.add(getPlayerHeadField(lane));
    final var data = new EmbedFieldBuilder<>(handleMatchups().stream().sorted().toList())
        .add("Matchup", PlayerMatchupData::getChampionString)
        .add("Games", playerMatchupData -> String.valueOf(playerMatchupData.standing().getGames()))
        .add("Winrate", PlayerMatchupData::getWinrate);
    fields.addAll(data.build());
    return fields;
  }

  private MessageEmbed.Field getPlayerHeadField(Lane lane) {
    return new MessageEmbed.Field(lane.getDisplayName() + ": " + player.getSummonerName() + "(" + getGames() + " Games - " + player.getLastRank().toString() + ")", "KDA: TBD - Gold: TBD - Damage: TBD - CS/VS: TBD", false);
    //TODO (Abgie) 30.03.2023: Playerstats
  }

  private List<PlayerMatchupData> handleMatchups() {
    final List<Object[]> matchupList = type.playerQuery(player, days).performance().get("enemy_champion", Champion.class).get("count(performance_id)", Integer.class).get("avg(_teamperf.win)", Double.class).groupBy(new SQLGroup("champion").having("count(performance_id) > 4")).ascending("avg(_teamperf.win)").list();
    return matchupList.stream().map(objects -> new PlayerMatchupData((Champion) objects[0],
        new Standing((int) ((int) objects[1] * (double) objects[2]), (int) ((int) objects[1] * (1 - ((double) objects[2])))))).toList();
  }

  private List<PlayerAnalyzerData> handlePicks() {
    final var time = LocalDateTime.now().minusDays(days);
    List<Object[]> presentPicks = type.playerQuery(player, days).selection().get("champion", Champion.class).get("count(selection_id)", Integer.class).groupBy("champion").descending("count(selection_id)").list();
    if (presentPicks.isEmpty()) {
      presentPicks = new Query<Selection>().get("champion", Champion.class).get("count(selection_id)", Integer.class)
          .where(Condition.inSubquery("game", new Query<Performance>().get("_teamperf.game", Game.class)
              .join(new JoinQuery<Performance, TeamPerf>("t_perf")).join(new JoinQuery<TeamPerf, Game>())
              .where("player", player).and("_game.start_time", time)
              .groupBy("lane").ascending("count(performance_id)")
          )).groupBy("champion").descending("count(selection_id)").list();
    }

    final List<Object[]> picksList = type.playerQuery(player, days).performance().get("champion", Champion.class).get("count(performance_id)", Integer.class).groupBy("champion").descending("count(performance_id)").list();
    final Map<Champion, Integer> pickMap = picksList.stream().collect(Collectors.toMap(objects -> (Champion) objects[0], objects -> ((Long) objects[1]).intValue(), (a, b) -> b));
    final List<Object[]> mmList = ScoutingGameType.MATCHMADE.playerQuery(player, days).performance().get("champion", Champion.class).get("count(performance_id)", Integer.class).groupBy("champion").descending("count(performance_id)").list();
    final Map<Champion, Integer> mmMap = mmList.stream().collect(Collectors.toMap(objects -> (Champion) objects[0], objects -> ((Long) objects[1]).intValue(), (a, b) -> b));
    final List<Object[]> winsList = ScoutingGameType.MATCHMADE.playerQuery(player, days).performance().get("champion", Champion.class).get("count(performance_id)", Integer.class).where("_teamperf.win", true).groupBy("champion").descending("count(performance_id)").list();
    final Map<Champion, Integer> winsMap = winsList.stream().collect(Collectors.toMap(objects -> (Champion) objects[0], objects -> ((Long) objects[1]).intValue(), (a, b) -> b));

    final List<Object[]> games = type.playerQuery(player, days).performance().get("count(performance_id)").list();
    final Long amountOfGames = ((Long) games.get(0)[0]);
    final Map<Champion, PlayerAnalyzerData> data = new HashMap<>();
    for (final Object[] presentPick : presentPicks) {
      final Champion champion = (Champion) presentPick[0];
      final int occurrences = ((Long) presentPick[1]).intValue();
      final int picks = pickMap.getOrDefault(champion, 0);
      final int mmGames = mmMap.getOrDefault(champion, 0);
      final int wins = winsMap.getOrDefault(champion, 0);
      if (picks > 0 || mmGames >= 10) {
        data.put(champion, new PlayerAnalyzerData(champion, occurrences * 1. / amountOfGames, picks, mmGames, wins));
      }
    }
    for (Object[] objects : picksList) {
      final Champion champion = (Champion) objects[0];
      final int amount = ((Long) (objects[1])).intValue();
      if (!data.containsKey(champion) && amount >= 10) {
        data.put(champion, new PlayerAnalyzerData(champion, 0, 0, amount, winsMap.get(champion)));
      }
    }
    final List<PlayerAnalyzerData> outputList = data.values().stream().sorted(Comparator.reverseOrder()).toList();
    return outputList.subList(0, Math.min(8, outputList.size()));
  }

  public int getGames() {
    final Object[] games = type.playerQuery(Long.class, player, days).performance().get("count(performance_id)").single();
    return (int) games[0];
  }

  public record PlayerAnalyzerData(Champion champion, double presence, int competitiveGames, int matchMadeGames,
                                   int matchMadeWins) implements Comparable<PlayerAnalyzerData> {
    public String getChampionString() {
      return champion.getName();
    }

    public String getCompetitiveString() {
      return Math.round(presence * 100) + "% - " + competitiveGames;
    }

    public String getMatchmadeString() {
      return matchMadeGames + " - " + Math.round(matchMadeWins * 100.0 / matchMadeGames) + "%";
    }

    @Override
    public int compareTo(@NotNull PlayerAnalyzerData o) {
      return Comparator.comparing(PlayerAnalyzerData::presence).compare(this, o);
    }
  }

  public record PlayerMatchupData(Champion champion, Standing standing) implements Comparable<PlayerMatchupData> {
    public String getChampionString() {
      return champion.getName();
    }

    public String getWinrate() {
      return standing.getWinrate().toString();
    }

    @Override
    public int compareTo(@NotNull PlayerMatchupData o) {
      return standing.compareTo(o.standing);
    }
  }
}
