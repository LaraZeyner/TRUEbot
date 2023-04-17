package de.zahrie.trues.api.database;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.calendar.ApplicationCalendar;
import de.zahrie.trues.api.calendar.CalendarBase;
import de.zahrie.trues.api.calendar.EventCalendar;
import de.zahrie.trues.api.calendar.scheduling.SchedulingCalendar;
import de.zahrie.trues.api.calendar.TeamCalendar;
import de.zahrie.trues.api.calendar.UserCalendar;
import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.betting.Bet;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannel;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.match.log.LineupMatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.model.OrgaCupMatch;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PRMMatch;
import de.zahrie.trues.api.coverage.match.model.ScheduleableMatch;
import de.zahrie.trues.api.coverage.match.model.Scrimmage;
import de.zahrie.trues.api.coverage.match.model.TournamentMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.PRMPlayer;
import de.zahrie.trues.api.coverage.player.model.Rank;
import de.zahrie.trues.api.coverage.season.OrgaCupSeason;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.ProfessionalSeason;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.SuperCupSeason;
import de.zahrie.trues.api.coverage.season.signup.SeasonSignup;
import de.zahrie.trues.api.coverage.stage.model.CalibrationStage;
import de.zahrie.trues.api.coverage.stage.model.CreationStage;
import de.zahrie.trues.api.coverage.stage.model.GroupStage;
import de.zahrie.trues.api.coverage.stage.model.PlayStage;
import de.zahrie.trues.api.coverage.stage.model.PlayoffStage;
import de.zahrie.trues.api.coverage.stage.model.SignupStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.stage.model.WaitingStage;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserGroup;
import de.zahrie.trues.api.logging.OrgaLog;
import de.zahrie.trues.api.logging.ServerLog;
import de.zahrie.trues.api.logging.TeamLog;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.game.Game;
import de.zahrie.trues.api.riot.matchhistory.performance.Performance;
import de.zahrie.trues.api.riot.matchhistory.selection.Selection;
import de.zahrie.trues.api.riot.matchhistory.teamperformance.TeamPerf;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class DatabaseEntityRegisterer implements Registerer<SessionFactory> {

  private SessionFactory buildSessionFactory() {
    final Configuration configuration = addClasses(List.of(
        Application.class,
        ApplicationCalendar.class,
        Bet.class,
        CalendarBase.class,
        CalibrationStage.class,
        Champion.class,
        CreationStage.class,
        CustomDiscordGroup.class,
        DiscordChannel.class,
        DiscordUser.class,
        DiscordUserGroup.class,
        EventCalendar.class,
        Game.class,
        GroupStage.class,
        OrgaCupMatch.class,
        League.class,
        LeagueTeam.class,
        Lineup.class,
        LineupMatchLog.class,
        Match.class,
        MatchLog.class,
        Membership.class,
        OrgaCupSeason.class,
        OrgaLog.class,
        Membership.class,
        OrgaTeam.class,
        Participator.class,
        Performance.class,
        Playday.class,
        PlayoffStage.class,
        Player.class,
        PlayStage.class,
        PRMMatch.class,
        PRMPlayer.class,
        PRMSeason.class,
        PRMTeam.class,
        ProfessionalSeason.class,
        Rank.class,
        ScheduleableMatch.class,
        SchedulingCalendar.class,
        Scrimmage.class,
        Season.class,
        SeasonSignup.class,
        Selection.class,
        ServerLog.class,
        SignupStage.class,
        Stage.class,
        SuperCupSeason.class,
        Team.class,
        TeamCalendar.class,
        TeamChannel.class,
        TeamLog.class,
        TeamPerf.class,
        TournamentMatch.class,
        UserCalendar.class,
        WaitingStage.class
    ));
    final Properties properties = configuration.getProperties();
    final StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder().applySettings(properties);
    return configuration.buildSessionFactory(registry.build());
  }

  private Configuration addClasses(List<Class<? extends Serializable>> classes) {
    final Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
    classes.forEach(configuration::addAnnotatedClass);
    return configuration;
  }

  @Override
  public SessionFactory register() {
    return buildSessionFactory();
  }
}
