package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(value = "orga_log", department = "team")
public class TeamLog implements Entity<TeamLog>, OrgaLog {
  @Serial
  private static final long serialVersionUID = 6603819381538268098L;

  private int id;
  private final LocalDateTime timestamp;
  private final DiscordUser invoker;
  private final DiscordUser target;
  private final String details;
  private final TeamLogAction action;
  private final OrgaTeam team;

  public TeamLog(DiscordUser invoker, DiscordUser target, String details, TeamLogAction action, OrgaTeam team) {
    this(LocalDateTime.now(), invoker, target, details, action, team);
  }

  public static TeamLog get(Object[] objects) {
    return new TeamLog(
        (int) objects[0],
        (LocalDateTime) objects[2],
        new Query<DiscordUser>().entity(objects[3]),
        new Query<DiscordUser>().entity(objects[4]),
        (String) objects[5],
        new SQLEnum<TeamLogAction>().of(objects[6]),
        new Query<OrgaTeam>().entity(objects[7])
    );
  }

  @Override
  public TeamLog create() {
    return new Query<TeamLog>().key("department", "team")
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
