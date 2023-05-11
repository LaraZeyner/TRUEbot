package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "orga_log", department = "team")
public class TeamLog extends OrgaLog implements Entity<TeamLog> {
  @Serial
  private static final long serialVersionUID = 7425349836183090767L;

  private final DiscordUser invoker;
  private final DiscordUser target;
  private final TeamLogAction action;
  private final OrgaTeam team;

  public TeamLog(DiscordUser invoker, DiscordUser target, String details, TeamLogAction action, OrgaTeam team) {
    this(LocalDateTime.now(), details, invoker, target, action, team);
  }

  public TeamLog(LocalDateTime timestamp, String details, DiscordUser invoker, DiscordUser target, TeamLogAction action, OrgaTeam team) {
    super(timestamp, details);
    this.invoker = invoker;
    this.target = target;
    this.action = action;
    this.team = team;
  }

  public TeamLog(int id, LocalDateTime timestamp, String details, DiscordUser invoker, DiscordUser target, TeamLogAction action, OrgaTeam team) {
    super(id, timestamp, details);
    this.invoker = invoker;
    this.target = target;
    this.action = action;
    this.team = team;
  }

  public static TeamLog get(List<Object> objects) {
    return new TeamLog(
        (int) objects.get(0),
        (LocalDateTime) objects.get(2),
        (String) objects.get(5),
        new Query<>(DiscordUser.class).entity(objects.get(3)),
        new Query<>(DiscordUser.class).entity(objects.get(4)),
        new SQLEnum<>(TeamLogAction.class).of(objects.get(6)),
        new Query<>(OrgaTeam.class).entity(objects.get(7))
    );
  }

  @Override
  public TeamLog create() {
    return new Query<>(TeamLog.class)
        .key("log_time", getTimestamp()).key("invoker", getInvoker()).key("target", getTarget())
        .key("details", getDetails()).key("action", action).key("team", team)
        .insert(this);
  }

  @Listing(Listing.ListingType.LOWER)
  public enum TeamLogAction {
    LINEUP_JOIN,
    LINEUP_LEAVE
  }
}
