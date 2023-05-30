package de.zahrie.trues.api.discord.builder.queryCustomizer;

import java.util.List;

import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Formatter;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.command.slash.Column;
import de.zahrie.trues.api.discord.command.slash.DBQuery;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.game.Game;
import de.zahrie.trues.api.riot.game.GameType;
import de.zahrie.trues.api.riot.performance.Performance;
import de.zahrie.trues.api.riot.performance.TeamPerf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NamedQuery {
  CHAMPIONS(new Query<>(""), new DBQuery(List.of(
      new Column("Championname", 16), new Column("Picks", 8)), false)),
  ORGA_GAMES(new Query<>(""), new DBQuery(List.of(
      new Column("Championname", 16), new Column("Picks", 8)), false)),
  TRYOUTS(new Query<>(Application.class, 50)
      .join(new JoinQuery<>(Application.class, DiscordUser.class, "discord_user"))
      .get("_discorduser.mention", String.class)
      .get(" - ", Formatter.of("lineup_role"), Formatter.of("position"))
      .get("waiting", Boolean.class)
      .where(Condition.notNull("waiting"))
      .descending("waiting"),
      new DBQuery(List.of(new Column("Nutzer"), new Column("Rolle"), new Column("wartend")), true)),



  PROFILE_RIOT_ACCOUNT(null, new DBQuery(List.of(new Column("Riot-Account")), true)),
  PROFILE_PRM_TEAM(null, new DBQuery(List.of(new Column("Team"), new Column("Infos")), true)),
  PROFILE_PRM_GAMES(new Query<>(Performance.class, 10)
      .join(new JoinQuery<>(Performance.class, TeamPerf.class, "t_perf"))
      .join(new JoinQuery<>(TeamPerf.class, Game.class, "_teamperf.game"))
      .join(new JoinQuery<>(Performance.class, Champion.class, "champion", "my"))
      .join(new JoinQuery<>(Performance.class, Champion.class, "enemy_champion", "enemy"))
      .join(new JoinQuery<>(Performance.class, Player.class, "player"))
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
}
