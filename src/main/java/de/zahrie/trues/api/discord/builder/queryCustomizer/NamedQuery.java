package de.zahrie.trues.api.discord.builder.queryCustomizer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.PlayerRank;
import de.zahrie.trues.api.coverage.player.model.Rank;
import de.zahrie.trues.api.coverage.season.OrgaCupSeason;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Formatter;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.datatypes.calendar.TimeFormat;
import de.zahrie.trues.api.datatypes.collections.SortedList;
import de.zahrie.trues.api.discord.command.slash.Column;
import de.zahrie.trues.api.discord.command.slash.DBQuery;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.riot.game.Selection;
import de.zahrie.trues.api.riot.performance.Performance;
import de.zahrie.trues.api.riot.performance.TeamPerf;
import de.zahrie.trues.util.Format;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public enum NamedQuery {
  CHAMPIONS(new Query<>(Selection.class, 50)
      .get("_champion.champion_name as Champion1", String.class)
      .get("concat(round(count(*) * 100 / (SELECT count(DISTINCT game) FROM selection JOIN game ON selection.game = game.game_id WHERE game_type = 0 AND orgagame = true)), '% - ', (SELECT concat(SUM(team_perf.win), ' : ', SUM(NOT team_perf.win)) FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id JOIN game ON game = game_id JOIN champion ON performance.champion = champion.champion_id WHERE game_type = 0 and orgagame = true AND champion.champion_name = Champion1))", String.class)
      .get("(SELECT concat(SUM(performance.kills), ' / ', SUM(performance.deaths), ' / ', SUM(performance.assists)) FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id JOIN game ON game = game_id JOIN champion ON performance.champion = champion.champion_id WHERE game_type = 0 and orgagame = true AND champion.champion_name = Champion1)", String.class)
      .join(new JoinQuery<>(Selection.class, Game.class))
      .join(new JoinQuery<>(Selection.class, Champion.class))
      .where("_game.game_type", GameType.TOURNAMENT).and("_game.orgagame", true)
      .groupBy("_selection.champion").descending("count(*)"),
      new DBQuery("Rekordchampions", "nur Prime League Spiele", List.of(
          new Column("Championname", 20), new Column("Picks", 20), new Column("KDA", 15)),
          true)),
  SEASON_CHAMPIONS(new Query<>(Selection.class, 50)
      .get("_champion.champion_name as Champion1", String.class)
      .get("concat(round(count(*) * 100 / (SELECT count(DISTINCT game) FROM selection JOIN game ON selection.game = game.game_id WHERE game_type = 0 AND orgagame = true)), '% - ', (SELECT concat(SUM(team_perf.win), ' : ', SUM(NOT team_perf.win)) FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id JOIN game ON game = game_id JOIN champion ON performance.champion = champion.champion_id WHERE game_type = 0 and orgagame = true AND champion.champion_name = Champion1))", String.class)
      .get("(SELECT concat(SUM(performance.kills), ' / ', SUM(performance.deaths), ' / ', SUM(performance.assists)) FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id JOIN game ON game = game_id JOIN champion ON performance.champion = champion.champion_id WHERE game_type = 0 and orgagame = true AND champion.champion_name = Champion1)", String.class)
      .join(new JoinQuery<>(Selection.class, Game.class))
      .join(new JoinQuery<>(Selection.class, Champion.class))
      .where("_game.game_type", GameType.TOURNAMENT).and("_game.orgagame", true)
      .and(Condition.Comparer.GREATER_EQUAL, "_game.start_time",
          new Query<>(PRMSeason.class).where("season_start <= now()").descending("season_start").entity().getRange().getStartTime())
      .groupBy("_selection.champion").descending("count(*)"),
      new DBQuery("Rekordchampions", "nur Prime League Spiele", List.of(
          new Column("Championname", 20), new Column("Picks", 20), new Column("KDA", 15)),
          true)),
  ORGA_ELOS(new Query<>("SELECT * FROM player WHERE 0"), new DBQuery(List.of(
      new Column("League-Account", 16), new Column("Teamname", 30), new Column("Punkte", 11)), true)),
  ORGA_SCHEDULE(new Query<>("SELECT * FROM player WHERE 0"), new DBQuery(List.of(
      new Column("Team 1", 30), new Column("Team 2", 30), new Column("Zeit/Ergebnis")), false)),
  ORGA_PRM(new Query<>(Performance.class, 100)
      .get("_player.lol_name as Leaguename", String.class)
      .get("count(*)", Integer.class)
      .get("concat(round(avg(_teamperf.win)*100), '%')", String.class)
      .get("concat(sum(_performance.kills), ' / ', sum(_performance.deaths), ' / ', sum(_performance.assists))", String.class)
      .get("(sum(_performance.kills) + sum(_performance.assists)) / sum(_performance.deaths)", Double.class)
      .get("sum(creeps) / 1000", Double.class)
      .get("sum(creeps) / count(*)", Double.class)
      .get("ROUND((AVG(creeps) - (SELECT AVG(creeps) FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id WHERE (game, lane, first) IN (SELECT game, lane, NOT first FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id JOIN game ON game = game_id WHERE orgagame = true AND lol_name = Leaguename))))", Integer.class)
      .get("sum(gold) / 1000", Double.class)
      .get("sum(gold) / count(*)", Double.class)
      .get("ROUND((AVG(gold) - (SELECT AVG(gold) FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id WHERE (game, lane, first) IN (SELECT game, lane, NOT first FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id JOIN game ON game = game_id WHERE orgagame = true AND lol_name = Leaguename))))", Integer.class)
      .get("sum(vision) / 1000", Double.class)
      .get("sum(vision) / count(*)", Double.class)
      .get("ROUND((AVG(vision) - (SELECT AVG(vision) FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id WHERE (game, lane, first) IN (SELECT game, lane, NOT first FROM performance JOIN player ON player = player_id JOIN team_perf ON t_perf = team_perf_id JOIN game ON game = game_id WHERE orgagame = true AND lol_name = Leaguename))))", Integer.class)
      .join(new JoinQuery<>(Performance.class, TeamPerf.class).col("t_perf"))
      .join(new JoinQuery<>(TeamPerf.class, Game.class).col("_teamperf.game"))
      .join(new JoinQuery<>(Performance.class, Player.class))
      .where("_game.game_type", GameType.TOURNAMENT).and("_player.played", true)
      .groupBy("_player.player_id").descending("count(*)"),
      new DBQuery("Rekordspieler", "nur Prime League Spiele",
          List.of(
              new Column("League-Account", true, true, 20),
              new Column("Games", true, true, 0),
              new Column("", false, true, 0),
              new Column("KDA-Ratio", true, true, 0),
              new Column("", false, true, 1),
              new Column("CS in Tsd.", true, true, 1),
              new Column("", false, true, 0),
              new Column("CSD", true, true, 0),
              new Column("Gold in Tsd.", true, true, 0),
              new Column("", false, true, 1),
              new Column("GoldD", true, true, 0),
              new Column("Dmg in Tsd.", true, true, 0),
              new Column("", false, true, 1),
              new Column("DmgD", true, true, 0),
              new Column("Vision", true, true, 0),
              new Column("", false, true, 0),
              new Column("VSD", true, true, 1)
          ),
          true)
  ),
  TRYOUTS(new Query<>(Application.class, 50)
      .join(new JoinQuery<>(Application.class, DiscordUser.class).col("discord_user"))
      .get("_discorduser.mention", String.class)
      .get(" - ", Formatter.of("lineup_role"), Formatter.of("position"))
      .get("waiting", Boolean.class)
      .where(Condition.notNull("waiting"))
      .descending("waiting"),
      new DBQuery(List.of(new Column("Nutzer"), new Column("Rolle"), new Column("wartend")), true)),


  PROFILE_RIOT_ACCOUNT(null, new DBQuery(List.of(new Column("Riot-Account")), true)),
  PROFILE_PRM_TEAM(null, new DBQuery(List.of(new Column("Team"), new Column("Infos")), true)),
  PROFILE_PRM_GAMES(new Query<>(Performance.class, 10)
      .join(new JoinQuery<>(Performance.class, TeamPerf.class).col("t_perf"))
      .join(new JoinQuery<>(TeamPerf.class, Game.class).col("_teamperf.game"))
      .join(new JoinQuery<>(Performance.class, Champion.class).as("my"))
      .join(new JoinQuery<>(Performance.class, Champion.class).col("enemy_champion").as("enemy"))
      .join(new JoinQuery<>(Performance.class, Player.class))
      .get(" - ", Formatter.of("_game.start_time", Formatter.CellFormat.AUTO),
          Formatter.of("IF(lane = 1, 'Top', IF(lane = 2, 'Jgl', IF(lane = 3, 'Mid', IF(lane = 4, 'Bot', IF(lane = 5, 'Sup', 'none')))))"))
      .get(" vs ", Formatter.of("_my.champion_name"), Formatter.of("_enemy.champion_name"))
      .get(" / ", Formatter.of("kills"), Formatter.of("deaths"),
          Formatter.of("CONCAT(_performance.assists, ' (', IF(_teamperf.win = false, 'N', 'S'), ')')"))
      .where("_game.game_type", GameType.TOURNAMENT).and("_player.lol_puuid", "?")
      .descending("_game.start_time"),
      new DBQuery("Prime League Spiele", "letzte 10 Spiele in der Prime League", List.of(
          new Column("Spielzeit"), new Column("Matchup"), new Column("Stats")), true));

  private final Query<?> query;
  private final DBQuery dbQuery;

  @Nullable
  public List<List<Object[]>> getCustom() {
    return switch (this) {
      case ORGA_SCHEDULE -> handleOrgaGames();
      case ORGA_ELOS -> handleOrgaElo();
      default -> null;
    };
  }

  private List<List<Object[]>> handleOrgaGames() {
    final PRMSeason lastPRMSeason = SeasonFactory.getLastPRMSeason();
    assert lastPRMSeason != null;
    final OrgaCupSeason lastInternSeason = SeasonFactory.getLastInternSeason();
    assert lastInternSeason != null;
    final List<Match> matches = new Query<>(Participator.class).get("_participator.coverage", Match.class)
        .join(new JoinQuery<>(Participator.class, OrgaTeam.class).col("team").ref("team"))
        .join(new JoinQuery<>(Participator.class, Match.class).col("coverage"))
        .where(Condition.Comparer.NOT_EQUAL, "_match.department", "scrimmage")
        .and(Condition.Comparer.GREATER_EQUAL, "_match.coverage_start", lastPRMSeason.getRange().getStartTime())
        .include(new Query<>(Match.class).where("_match.department", "intern")
            .and(Condition.Comparer.GREATER_EQUAL, "_match.coverage_start", lastInternSeason.getRange().getStartTime()))
        .convertList(Match.class);

    final String[] home = matches.stream().map(Match::getHome).map(Participator::getTeam)
        .map(team -> team == null ? "null" : team.getName()).toArray(String[]::new);
    final String[] guest = matches.stream().map(Match::getGuest).map(Participator::getTeam)
        .map(team -> team == null ? "null" : team.getName()).toArray(String[]::new);
    final String[] result = matches.stream().map(match -> (match.getResult().toString().equals("-:-") ?
        TimeFormat.AUTO(match.getStart()) : match.getResult().toString())).toArray(String[]::new);
    return List.of(List.of(home, guest, result));
  }

  private List<List<Object[]>> handleOrgaElo() {
    final List<List<PlayerRank>> ranks = new SortedList<>();
    IntStream.range(1, Rank.RankTier.values().length)
        .mapToObj(i -> new SortedList<>(Comparator.comparing(PlayerRank::getRank).reversed())).forEach(ranks::add);

    for (Player player : new Query<>(Player.class).where(Condition.notNull("_player.discord_user")).entityList()) {
      final PlayerRank current = player.getRanks().getCurrent();
      final Rank.RankTier tier = current.getRank().tier();
      if (!tier.equals(Rank.RankTier.UNRANKED)) ranks.get(Rank.RankTier.CHALLENGER.ordinal() - tier.ordinal()).add(current);
    }

    return ranks.stream().<List<Object[]>>map(data1 -> List.of(
        data1.stream().map(playerRank -> playerRank.getPlayer().getSummonerName()).toArray(String[]::new),
        data1.stream().map(playerRank -> playerRank.getPlayer().getTeam()).map(t -> t == null ? "null" : t.getName()).toArray(String[]::new),
        data1.stream().map(r -> r.getRank().division().getPoints() + r.getRank().points() + " LP (" + r.getWinrate().format(Format.MEDIUM) + ")").toArray(String[]::new)
    )).collect(Collectors.toList());
  }
}
