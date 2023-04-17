package de.zahrie.trues.api.coverage.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.model.Standing;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.number.TrueNumber;
import de.zahrie.trues.api.discord.builder.embed.EmbedFieldBuilder;
import de.zahrie.trues.api.riot.matchhistory.KDA;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.Util;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record TeamAnalyzer(Team team, ScoutingGameType type, int days) {

  public List<MessageEmbed.Field> analyzeChampions() {
    return new EmbedFieldBuilder<>(handleChampions())
        .num("Champion", championData -> championData.champion.getName())
        .add("Picks", ChampionData::getPicksString)
        .add("KDA", ChampionData::getKDAString)
        .build();
  }

  public List<ChampionData> handleChampions() {
    final List<Object[]> presence = type.teamQuery(team, days).selection("champion, count(s)", "GROUP BY champion ORDER BY count(s) desc");
        getObjects("Presence");
    final List<Object[]> stats = type.teamQuery(team, days).performance("champion, count(p), sum(if(teamPerformance.win, 1, 0)), sum(kda.kills), sum(kda.deaths), sum(kda.assists)", "GROUP BY champion ORDER BY count(p) desc");

    final Map<Champion, ChampionStats> championStats = stats.stream().collect(Collectors.toMap(stat -> (Champion) stat[0],
        stat -> new ChampionStats(new Standing((int) stat[2], (int) stat[1] - (int) stat[2]),
            new KDA((short) stat[3], (short) stat[4], (short) stat[5])), (a, b) -> b));
    final List<Object[]> games = type.teamQuery(team, days).performance("count(distinct teamPerformance.game)");
    final int amountOfGames = (int) games.get(0)[0];
    return presence.stream().map(objects -> new ChampionData((Champion) objects[0], (int) objects[1] * 1. / amountOfGames, championStats.get((Champion) objects[0]))).toList();

  }

  public List<MessageEmbed.Field> analyzeSchedule() {
    return new EmbedFieldBuilder<>(getMatches())
        .add("Spielzeit", match -> TimeFormat.DISCORD.of(match.getStart()))
        .add("Gegner", match -> Util.avoidNull(match.getOpponentOf(team), "keine Daten", Team::getName))
        .add("Ergebnis", Match::getResult).build();
  }

  public List<Match> getMatches() {
    final LocalDateTime startTime = LocalDateTime.now().minusDays(days);
    return QueryBuilder.hql(Match.class, "SELECT coverage FROM Participator WHERE team = " + team.getId() + " AND (coverage.start >= " +
        startTime + " OR coverage.result = '-:-') ORDER BY coverage.start").list();
  }

  public List<MessageEmbed.Field> analyzeHistory(int page) {
    final List<MessageEmbed.Field> fields = new ArrayList<>();

    final List<TeamPerf> games = type.teamQuery(TeamPerf.class, team, days).performance("teamPerformance", "ORDER BY teamPerformance.game.start desc");
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
        .add("Matchup", Performance::getMatchup)
        .add("Stats", Performance::getStats)
        .build();
  }

  private List<Object[]> getObjects(String key) {
    final var time = LocalDateTime.now().minusDays(days);
    return switch (type) {
      case PRM_ONLY -> Database.Find.findObjectList(new String[]{"team", "start"}, new Object[]{team, time}, "TeamPerf.get" + key + "PRM");
      case PRM_CLASH -> Database.Find.findObjectList(new String[]{"team", "start"}, new Object[]{team, time}, "TeamPerf.get" + key + "PRMClash");
      case TEAM_GAMES -> Database.Find.findObjectList(new String[]{"team", "start"}, new Object[]{team, time}, "TeamPerf.get" + key + "TeamGames");
      case MATCHMADE -> Database.Find.findObjectList(new String[]{"team", "start"}, new Object[]{team, time}, "TeamPerf.get" + key + "Matchmade");
    };
  }

  public record ChampionData(Champion champion, double presence, ChampionStats stats) {
    public String getPicksString() {
      return new TrueNumber(presence).percentValue() + " - " + stats.standing.toString();
    }

    public String getKDAString() {
      return stats.kda.getKills() + " / " + stats.kda.getDeaths() + " / " + stats.kda.getAssists();
    }
  }

  public record ChampionStats(Standing standing, KDA kda) {
  }
}
