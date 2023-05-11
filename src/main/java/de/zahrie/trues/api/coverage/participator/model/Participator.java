package de.zahrie.trues.api.coverage.participator.model;

import java.io.Serial;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.teamchannel.TeamChannelType;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.match.log.MatchLogBuilder;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.TeamLineup;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.coverage.team.model.TeamImpl;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Team als Teilnehmer an einem Match
 */
@Getter
@Setter
@Table("coverage_team")
@ExtensionMethod(SQLUtils.class)
public class Participator implements Entity<Participator>, Comparable<Participator> {
  @Serial
  private static final long serialVersionUID = -547972848562058467L;

  public static Participator ADMIN(Match match) {
    return new Participator(match, false, new TeamImpl("Administration", "Admin"));
  }

  private int id; // coverage_team_id
  private final Match match; // coverage
  private final boolean home; // first
  private final ParticipatorRoute route;
  private Team team; // team
  private short wins = 0; // wins
  private Long discordEventId; // discord_event
  private Long messageId; // discord_message

  public Participator(Match match, boolean home) {
    this(match, home, (ParticipatorRoute) null);
  }

  public Participator(Match match, boolean home, ParticipatorRoute route) {
    this.match = match;
    this.home = home;
    this.route = route;
  }

  public Participator(Match match, boolean home, Team team) {
    this.match = match;
    this.home = home;
    this.route = null;
    this.team = team;
  }

  public Participator(int id, Match match, boolean home, Team team, short wins, ParticipatorRoute route, Long discordEventId, Long messageId) {
    this.id = id;
    this.match = match;
    this.home = home;
    this.team = team;
    this.wins = wins;
    this.route = route;
    this.discordEventId = discordEventId;
    this.messageId = messageId;
  }

  public static Participator get(List<Object> objects) {
    return new Participator(
        (int) objects.get(0),
        new Query<>(Match.class).entity(objects.get(1)),
        (boolean) objects.get(2),
        new Query<>(Team.class).entity(objects.get(6)),
        objects.get(7).shortValue(),
        new ParticipatorRoute(new Query<>(League.class).entity(objects.get(3)), new SQLEnum<>(ParticipatorRoute.RouteType.class).of(objects.get(4)), objects.get(5).shortValue()),
        (Long) objects.get(8),
        (Long) objects.get(9)
    );
  }

  private TeamLineup teamLineupHandler;

  @NonNull
  public TeamLineup getTeamLineup() {
    return getTeamLineup(ScoutingGameType.TEAM_GAMES, 180);
  }

  @NonNull
  public TeamLineup getTeamLineup(ScoutingGameType gameType, int days) {
    if (gameType.equals(ScoutingGameType.TEAM_GAMES) && days == 180) {
      if (teamLineupHandler == null) this.teamLineupHandler = new TeamLineup(this, gameType, days);
      return teamLineupHandler;
    }
    return new TeamLineup(this, gameType, days);
  }

  @Override
  public Participator create() {
    final League league = route == null ? null : route.getLeague();
    final ParticipatorRoute.RouteType routeType = route == null ? null : route.getType();
    final Short routeValue = route == null ? null : route.getValue();
    return new Query<>(Participator.class)
        .key("coverage", match).key("first", home).key("route_group", league).key("route_type", routeType).key("route_value", routeValue)
        .col("team", team).col("wins", wins).col("discord_event", discordEventId).col("discord_message", messageId)
        .insert(this);
  }

  @Override
  public void delete() {
    setTeam(null);
    setWins((short) 0);
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
    new Query<>(Participator.class).col("discord_event", discordEventId).update(id);
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
    new Query<>(Participator.class).col("discord_message", messageId).update(id);
  }

  public void setTeam(@Nullable Team team) {
    if (match == null || match.checkAddParticipatingTeam(this, team)) {
      new Query<>(Lineup.class).where("coverage_team", this).delete(List.of());
      this.team = team;
      new Query<>(Participator.class).col("team", team).update(id);
      if (match != null) match.updateResult();
    }
  }

  public void setWins(int wins) {
    if (this.wins != wins) {
      this.wins = (short) wins;
      new Query<>(Participator.class).col("wins", wins).update(id);
    }
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
