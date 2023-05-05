package de.zahrie.trues.api.coverage.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.number.TrueNumber;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.performance.TeamPerf;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.Util;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record TeamAnalyzer(TeamBase team, ScoutingGameType type, int days) {

  public List<MessageEmbed.Field> analyzeChampions() {
    return new EmbedFieldBuilder<>(handleChampions())
        .num("Champion", championData -> championData.champion.getName())
        .add("Picks", ChampionData::getPicksString)
        .add("KDA", ChampionData::getKDAString)
        .build();
  }

  public List<ChampionData> handleChampions() {

    final List<Object[]> presence = type.teamQuery(team, days).selection().get("champion", Champion.class).get("count(selection_id)", Integer.class).groupBy("champion").descending("count(selection_id)").list();
    final List<Object[]> stats = type.teamQuery(team, days).performance().get("champion", Champion.class).get("count(performance_id)", Integer.class).get("sum(if(_teamperf.win, 1, 0))", Integer.class).get("sum(performance.kills)", Integer.class).get("sum(performance.deaths)", Integer.class).get("sum(performance.assists)", Integer.class).groupBy("champion").descending("count(performance_id)").list();

    final Map<Champion, ChampionStats> championStats = stats.stream().collect(Collectors.toMap(stat -> (Champion) stat[0],
        stat -> new ChampionStats(new Standing((int) stat[2], (int) stat[1] - (int) stat[2]),
            new KDA((short) stat[3], (short) stat[4], (short) stat[5])), (a, b) -> b));
    final Object[] games = type.teamQuery(team, days).performance().get("count(distinct teamPerformance.game)").single();
    final int amountOfGames = (int) games[0];
    return presence.stream().map(objects -> new ChampionData((Champion) objects[0], (int) objects[1] * 1. / amountOfGames, championStats.get((Champion) objects[0]))).toList();

  }

  public List<MessageEmbed.Field> analyzeSchedule() {
    return new EmbedFieldBuilder<>(getMatches())
        .add("Spielzeit", match -> TimeFormat.DISCORD.of(match.getStart()))
        .add("Gegner", match -> Util.avoidNull(match.getOpponentOf(team), "keine Daten", TeamBase::getName))
        .add("Ergebnis", match -> match.getResult().toString()).build();
  }

  public List<Match> getMatches() {
    final LocalDateTime startTime = LocalDateTime.now().minusDays(days);
    return new Query<Participator>().get("coverage", Match.class)
        .join(new JoinQuery<Participator, Match>("coverage", "_match"))
        .keep("team", team)
        .where(Condition.Comparer.GREATER_EQUAL, "_match.coverage_start", startTime).or("_match.result", "-:-")
        .ascending("_match.coverage_start").convertList(Match.class);
  }

  public List<MessageEmbed.Field> analyzeHistory(int page) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();

    final List<TeamPerf> games = type.teamQuery(TeamPerf.class, team, days).performance().get("t_perf", TeamPerf.class).descending("_game.start_time").convertList(TeamPerf.class);
    if (games.size() <= (page - 1) * 6) {
      page = (int) Math.ceil(games.size() / 6.);
    }
    for (int i = 0; i < 6; i++) {
      final int count = i + (page - 1) * 6;
      if (count >= games.size()) break;
      final TeamPerf teamPerf = games.get(count);
      fields.addAll(getFieldsOfTeamPerformance(teamPerf));
    }
    return fields;
  }

  private List<MessageEmbed.Field> getFieldsOfTeamPerformance(TeamPerf teamPerf) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();

    final String head = TimeFormat.DISCORD.of(teamPerf.getGame().getStart()) + teamPerf.getGame().getType().name() + ": " + teamPerf.getWinString() +
        Util.avoidNull(teamPerf.getOpposingTeam(), "", primeTeam -> " vs " + primeTeam.getName());
    final String description = "(" + teamPerf.getKda().toString() + ") nach " + teamPerf.getGame().getDuration();
    fields.add(new MessageEmbed.Field(head, description, false));

    return new EmbedFieldBuilder<>(fields, teamPerf.getPerformances().stream().sorted().toList())
        .add("Spielername", Performance::getPlayername)
        .add("Matchup", performance -> performance.getMatchup().toString())
        .add("Stats", Performance::getStats)
        .build();
  }

  public record ChampionData(Champion champion, double presence, ChampionStats stats) {
    public String getPicksString() {
      return new TrueNumber(presence).percentValue() + " - " + stats.standing.toString();
    }

    public String getKDAString() {
      return stats.kda.kills() + " / " + stats.kda.deaths() + " / " + stats.kda.assists();
    }
  }

  public record ChampionStats(Standing standing, KDA kda) {
  }
}
