package de.zahrie.trues.util.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import de.zahrie.trues.PrimeData;
import de.zahrie.trues.models.betting.Bet;
import de.zahrie.trues.models.calendar.ApplicationCalendar;
import de.zahrie.trues.models.calendar.RepeatedSchedulingCalendar;
import de.zahrie.trues.models.calendar.SchedulingCalendar;
import de.zahrie.trues.models.calendar.TeamCalendar;
import de.zahrie.trues.models.calendar.UserCalendar;
import de.zahrie.trues.models.community.OrgaTeam;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.coverage.Group;
import de.zahrie.trues.models.coverage.Lineup;
import de.zahrie.trues.models.coverage.Participator;
import de.zahrie.trues.models.coverage.Playday;
import de.zahrie.trues.models.coverage.Stage;
import de.zahrie.trues.models.coverage.match.Match;
import de.zahrie.trues.models.coverage.match.PrimeMatch;
import de.zahrie.trues.models.coverage.match.ScheduleableMatch;
import de.zahrie.trues.models.coverage.match.Scrimmage;
import de.zahrie.trues.models.coverage.match.TournamentMatch;
import de.zahrie.trues.models.coverage.season.BetSeason;
import de.zahrie.trues.models.coverage.season.PrimeSeason;
import de.zahrie.trues.models.coverage.season.Season;
import de.zahrie.trues.models.discord.DiscordChannel;
import de.zahrie.trues.models.discord.DiscordGroup;
import de.zahrie.trues.models.discord.member.DiscordMember;
import de.zahrie.trues.models.discord.member.DiscordMemberGroup;
import de.zahrie.trues.models.discord.permission.ChannelPermissionPattern;
import de.zahrie.trues.models.discord.permission.CommandPermission;
import de.zahrie.trues.models.discord.permission.PermissionPattern;
import de.zahrie.trues.models.logging.OrgaLog;
import de.zahrie.trues.models.player.Player;
import de.zahrie.trues.models.riot.Champion;
import de.zahrie.trues.models.riot.matchhistory.Game;
import de.zahrie.trues.models.riot.matchhistory.Performance;
import de.zahrie.trues.models.riot.matchhistory.Selection;
import de.zahrie.trues.models.riot.matchhistory.TeamPerf;
import de.zahrie.trues.models.team.PrimeTeam;
import de.zahrie.trues.models.team.Team;
import de.zahrie.trues.models.voting.Voting;
import de.zahrie.trues.models.voting.VotingEntry;
import de.zahrie.trues.util.logger.Logger;
import jakarta.persistence.NoResultException;
import jakarta.persistence.metamodel.EntityType;
import lombok.val;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

/**
 * Created by Lara on 24.03.2022 for TRUES
 */
public final class Database {

  private static final SessionFactory sessionFactory = buildSessionFactory();


  private static SessionFactory buildSessionFactory() {
    final Configuration configuration = addClasses(List.of(
        ApplicationCalendar.class,
        Bet.class,
        BetSeason.class,
        Champion.class,
        ChannelPermissionPattern.class,
        CommandPermission.class,
        DiscordChannel.class,
        DiscordGroup.class,
        DiscordMember.class,
        DiscordMemberGroup.class,
        Game.class,
        Group.class,
        Lineup.class,
        Match.class,
        OrgaLog.class,
        OrgaMember.class,
        OrgaTeam.class,
        Participator.class,
        Performance.class,
        PermissionPattern.class,
        Playday.class,
        Player.class,
        PrimeMatch.class,
        PrimeSeason.class,
        PrimeTeam.class,
        RepeatedSchedulingCalendar.class,
        ScheduleableMatch.class,
        SchedulingCalendar.class,
        Scrimmage.class,
        Season.class,
        Selection.class,
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

  public static void save(Object object) {
    PrimeData.getInstance().getSession().merge(object);
  }

  public static <T> List<T> findList(Class<T> entityClass) {
    final Session session = PrimeData.getInstance().getSession();
    final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
    final String entityClassName = storedEntity.getName();

    try {
      final Query<Object[]> query = session.getNamedQuery(entityClassName + ".findAll");
      return query.list().stream().map(t -> (T) t[0]).collect(Collectors.toList());
    } catch (ClassCastException ex) {
      final Query<T> query = session.getNamedQuery(entityClassName + ".findAll");
      return query.list();
    }
  }

  public static <T> List<T> findList(Class<T> entityClass, String identifier, boolean bool) {
    if (bool) {
      final Session session = PrimeData.getInstance().getSession();
      final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
      final String entityClassName = storedEntity.getName();

      return getData(entityClassName + "." + identifier);
    }
    return findList(entityClass, identifier);
  }

  public static <T> List<T> getData(String queryName) {
    final Query<T> query = PrimeData.getInstance().getSession().getNamedQuery(queryName);
    return query.list();
  }

  public static <T> List<T> findList(Class<T> entityClass, long primaryKey) {
    return findList(entityClass, String.valueOf(primaryKey));
  }

  public static <T> List<T> findList(Class<T> entityClass, String primaryKey) {
    final Query<T> query = perform(entityClass, primaryKey);
    return query.list();
  }

  public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values) {
    return findList(entityClass, params, values, "findBy");
  }

  public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
    final Query<T> query = performWithSubquery(entityClass, params, values, subQuery);
    return query.list();
  }

  public static <T> T find(Class<T> entityClass, long id) {
    return PrimeData.getInstance().getSession().get(entityClass, id);
  }

  public static <T> T find(Class<T> entityClass, String[] params, Object[] values) {
    try {
      return performSingle(entityClass, params, values, "findBy");
    } catch (NoResultException exception) {
      return null;
    }
  }

  public static <T> T find(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
    try {
      return performSingle(entityClass, params, values, subQuery);
    } catch (NoResultException exception) {
      return null;
    }
  }

  public static <T> boolean has(Class<T> entityClass, long primaryKey) {
    return has(entityClass, String.valueOf(primaryKey));
  }

  public static <T> boolean has(Class<T> entityClass, String primaryKey) {
    try {
      performSingle(entityClass, primaryKey);
      return true;
    } catch (NoResultException exception) {
      return false;
    }
  }

  public static <T> boolean has(Class<T> entityClass, String[] params, Object[] values) {
    try {
      performSingle(entityClass, params, values, "findBy");
      return true;
    } catch (NoResultException exception) {
      return false;
    }
  }

  public static <T> boolean has(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
    try {
      performSingle(entityClass, params, values, subQuery);
      return true;
    } catch (NoResultException exception) {
      return false;
    }
  }

  private static <T> T performSingle(Class<T> entityClass, String primaryKey) {
    final var query = perform(entityClass, primaryKey);
    return query.setMaxResults(1).getSingleResult();
  }

  private static <T> Query<T> perform(Class<T> entityClass, String primaryKey) {
    final var session = PrimeData.getInstance().getSession();
    final var storedEntity = session.getMetamodel().entity(entityClass);
    final var entityClassName = storedEntity.getName();

    final var query = session.getNamedQuery(entityClassName + ".findById");
    final var ret = new HashMap<Class<?>, Number>();

    try {
      ret.put(Long.class, Long.parseLong(primaryKey));
      ret.put(Integer.class, Integer.parseInt(primaryKey));
      ret.put(Short.class, Short.parseShort(primaryKey));
      ret.put(Byte.class, Byte.parseByte(primaryKey));

    } catch (NumberFormatException exception) {
      Logger.getLogger("Entity-Query").throwing(exception);
    }

    final Class<?> pk = query.getParameter("pk").getParameterType();
    query.setParameter("pk", pk.equals(String.class) ? primaryKey : ret.get(pk));
    return query;
  }

  private static <T> T performSingle(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    try {
      val query = performWithSubquery(entityClass, params, values, subquery);
      return query.setMaxResults(1).getSingleResult();
    } catch (ClassCastException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  private static <T> Query<T> performWithSubquery(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    val session = PrimeData.getInstance().getSession();
    val storedEntity = session.getMetamodel().entity(entityClass);
    val entityClassName = storedEntity.getName();

    val query = session.getNamedQuery(entityClassName + "." + subquery);

    for (int i = 0; i < params.length; i++) {
      final String param = params[i];
      final Object value = values[i];
      query.setParameter(param, value);
    }
    return query;
  }


  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public static void shutdown() {
    getSessionFactory().close();
  }


}