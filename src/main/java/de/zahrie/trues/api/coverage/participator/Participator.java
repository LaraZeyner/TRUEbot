package de.zahrie.trues.api.coverage.participator;

import java.io.Serial;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.lineup.model.Lineup;
import de.zahrie.trues.api.coverage.match.log.MatchLogBuilder;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.util.Nunu;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Team als Teilnehmer an einem Match
 */
@Getter
@Setter
@Table("coverage_team")
public class Participator implements Entity<Participator>, Comparable<Participator> {
  @Serial
  private static final long serialVersionUID = -547972848562058467L;

  public static Participator ADMIN(Match match) {
    return new Participator(match, false, new Team("Administration", "Admin"));
  }

  private int id; // coverage_team_id
  private final Match match; // coverage
  private final boolean home; // first
  private final ParticipatorRoute route;
  private TeamBase team; // team
  private short wins = 0; // wins
  private Long discordEventId; // discord_event
  private Long messageId; // discord_message
  private final Set<Lineup> lineups = new TreeSet<>();

  public Participator(Match match, boolean home) {
    this(match, home, (ParticipatorRoute) null);
  }

  public Participator(Match match, boolean home, ParticipatorRoute route) {
    this.match = match;
    this.home = home;
    this.route = route;
  }

  public Participator(Match match, boolean home, TeamBase team) {
    this.match = match;
    this.home = home;
    this.route = null;
    this.team = team;
  }

  public Participator(int id, Match match, boolean home, TeamBase team, short wins, ParticipatorRoute route, Long discordEventId, Long messageId) {
    this.id = id;
    this.match = match;
    this.home = home;
    this.team = team;
    this.wins = wins;
    this.route = route;
    this.discordEventId = discordEventId;
    this.messageId = messageId;
  }

  public static Participator get(Object[] objects) {
    final Participator participator = new Participator(
        (int) objects[0],
        new Query<Match>().entity(objects[1]),
        (boolean) objects[2],
        new Query<Team>().entity(objects[3]),
        (short) objects[4],
        new ParticipatorRoute(new Query<League>().entity(objects[5]), new SQLEnum<ParticipatorRoute.RouteType>().of(objects[6]), (Short) objects[7]),
        (Long) objects[8],
        (Long) objects[9]
    );
    participator.lineups.addAll(participator.determineLineups());
    return participator;
  }

  @Override
  public Participator create() {
    final League league = route == null ? null : route.getLeague();
    final ParticipatorRoute.RouteType routeType = route == null ? null : route.getType();
    final Short routeValue = route == null ? null : route.getValue();
    return new Query<Participator>()
        .key("coverage", match).key("first", home).key("route_group", league).key("route_type", routeType).key("route_value", routeValue)
        .col("team", team).col("wins", wins).col("discord_event", discordEventId).col("discord_message", messageId)
        .insert(this, p -> p.lineups.addAll(determineLineups()));
  }

  public void createScheduledEvent() {
    final OrgaTeam orgaTeam = team.getOrgaTeam();
    if (orgaTeam == null) return;

    final IPermissionContainer practiceChannel = orgaTeam.getChannels().get(TeamChannelType.PRACTICE).getChannel();
    Nunu.getInstance().getGuild().createScheduledEvent(match.toString(), practiceChannel,
        match.getStart().atZone(ZoneId.systemDefault()).toOffsetDateTime())
        .setDescription(new MatchLogBuilder(match, team).toString())
        .setEndTime(match.getExpectedTimeRange().getEndTime().atZone(ZoneId.systemDefault()).toOffsetDateTime())
        .queue(scheduledEvent -> setDiscordEventId(scheduledEvent.getIdLong()));
    new Query<Participator>().col("discord_event", discordEventId).update(id);
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
    new Query<Participator>().col("discord_message", messageId).update(id);
  }

  public void setTeam(TeamBase team) {
    if (match == null || match.checkAddParticipatingTeam(this, team)) {
      this.team = team;
      new Query<Participator>().col("team", team).update(id);
    }
  }

  public void setWins(short wins) {
    this.wins = wins;
    new Query<Participator>().col("wins", wins).update(id);
  }

  public String getAbbreviation() {
    if (team == null) return route == null ? "TBD" : route.toString();
    return team.getAbbreviation();
  }

  public String getName() {
    if (team == null) return route == null ? "TBD" : route.toString();
    return team.getName();
  }

  @Override
  public void delete() {
    this.team = null;
    this.wins = 0;
    for (final Lineup lineup : lineups) {
      lineup.delete();
    }
  }

  private List<Lineup> determineLineups() {
    return new Query<Lineup>().where("coverage_team", this).entityList();
  }

  public ParticipatorImpl get() {
    return new ParticipatorImpl(this);
  }


  @Override
  public int compareTo(@NotNull Participator o) {
    return Comparator.comparing(Participator::getMatch).thenComparing(Participator::isHome).compare(this, o);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final Participator that)) return false;
    if (id == that.getId()) return true;
    return Objects.equals(getMatch(), that.getMatch()) && Objects.equals(getTeam(), that.getTeam());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getMatch(), getTeam());
  }
}
