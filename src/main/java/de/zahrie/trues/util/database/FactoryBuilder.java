package de.zahrie.trues.util.database;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import de.zahrie.trues.api.coverage.stage.model.CalibrationStage;
import de.zahrie.trues.api.coverage.stage.model.SignupStage;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.match.log.LineupMatchLog;
import de.zahrie.trues.api.coverage.match.log.MatchLog;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.match.model.PrimeMatch;
import de.zahrie.trues.api.coverage.match.model.ScheduleableMatch;
import de.zahrie.trues.api.coverage.match.model.Scrimmage;
import de.zahrie.trues.api.coverage.match.model.TournamentMatch;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.playday.Playday;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.player.model.PrimePlayer;
import de.zahrie.trues.api.coverage.player.model.Rank;
import de.zahrie.trues.api.coverage.season.ProfessionalSeason;
import de.zahrie.trues.api.coverage.season.PrimeSeason;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.models.betting.Bet;
import de.zahrie.trues.models.calendar.ApplicationCalendar;
import de.zahrie.trues.models.calendar.RepeatedSchedulingCalendar;
import de.zahrie.trues.models.calendar.SchedulingCalendar;
import de.zahrie.trues.models.calendar.TeamCalendar;
import de.zahrie.trues.models.calendar.UserCalendar;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.discord.DiscordChannel;
import de.zahrie.trues.models.discord.DiscordGroup;
import de.zahrie.trues.models.discord.member.DiscordMember;
import de.zahrie.trues.models.discord.member.DiscordMemberGroup;
import de.zahrie.trues.models.discord.permission.ChannelPermissionPattern;
import de.zahrie.trues.models.discord.permission.CommandPermission;
import de.zahrie.trues.models.discord.permission.PermissionPattern;
import de.zahrie.trues.models.logging.OrgaLog;
import de.zahrie.trues.models.riot.Champion;
import de.zahrie.trues.models.riot.matchhistory.Game;
import de.zahrie.trues.models.riot.matchhistory.Performance;
import de.zahrie.trues.models.riot.matchhistory.Selection;
import de.zahrie.trues.models.riot.matchhistory.TeamPerf;
import de.zahrie.trues.models.voting.Voting;
import de.zahrie.trues.models.voting.VotingEntry;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class FactoryBuilder {

  private static SessionFactory buildSessionFactory() {
    final Configuration configuration = addClasses(List.of(
        ApplicationCalendar.class,
        Bet.class,
        ProfessionalSeason.class,
        CalibrationStage.class,
        Champion.class,
        ChannelPermissionPattern.class,
        CommandPermission.class,
        DiscordChannel.class,
        DiscordGroup.class,
        DiscordMember.class,
        DiscordMemberGroup.class,
        Game.class,
        League.class,
        Lineup.class,
        LineupMatchLog.class,
        Match.class,
        MatchLog.class,
        OrgaLog.class,
        OrgaMember.class,
        OrgaTeam.class,
        Participator.class,
        Performance.class,
        PermissionPattern.class,
        Playday.class,
        Player.class,
        PrimeMatch.class,
        PrimePlayer.class,
        PrimeSeason.class,
        PrimeTeam.class,
        Rank.class,
        RepeatedSchedulingCalendar.class,
        ScheduleableMatch.class,
        SchedulingCalendar.class,
        Scrimmage.class,
        Season.class,
        Selection.class,
        SignupStage.class,
        Stage.class,
        Team.class,
        TeamCalendar.class,
        TeamPerf.class,
        TournamentMatch.class,
        UserCalendar.class,
        Voting.class,
        VotingEntry.class
    ));
    final Properties properties = configuration.getProperties();
    final StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder().applySettings(properties);
    return configuration.buildSessionFactory(registry.build());
  }

  private static Configuration addClasses(List<Class<? extends Serializable>> classes) {
    final Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
    classes.forEach(configuration::addAnnotatedClass);
    return configuration;
  }

  public static SessionFactory build() {
    return FactoryBuilder.buildSessionFactory();
  }

}
