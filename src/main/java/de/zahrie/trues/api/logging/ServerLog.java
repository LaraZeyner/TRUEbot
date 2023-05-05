package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.time.LocalDateTime;

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
@Table(value = "orga_log", department = "member")
public class ServerLog implements Entity<ServerLog>, OrgaLog {
  @Serial
  private static final long serialVersionUID = -1495547582387523247L;

  private int id;
  private final LocalDateTime timestamp;
  private final DiscordUser invoker;
  private final DiscordUser target;
  private final String details;
  private final ServerLogAction action;

  public ServerLog(DiscordUser invoker, DiscordUser target, String details, ServerLogAction action) {
    this.timestamp = LocalDateTime.now();
    this.invoker = invoker;
    this.target = target;
    this.details = details;
    this.action = action;
  }

  public ServerLog(DiscordUser target, String details, ServerLogAction action) {
    this(LocalDateTime.now(), null, target, details, action);
  }

  public static ServerLog get(Object[] objects) {
    return new ServerLog(
        (int) objects[0],
        (LocalDateTime) objects[2],
        new Query<DiscordUser>().entity(objects[3]),
        new Query<DiscordUser>().entity(objects[4]),
        (String) objects[5],
        new SQLEnum<ServerLogAction>().of(objects[6])
    );
  }

  @Override
  public ServerLog create() {
    return new Query<ServerLog>().key("department", "member")
        .key("log_time", getTimestamp()).key("target", getTarget()).key("details", getDetails()).key("action", action)
        .insert(this);
  }

  @Listing(Listing.ListingType.LOWER)
  public enum ServerLogAction {
    APPLICATION_CREATED,
    SERVER_JOIN,
    SERVER_LEAVE,
    COMMAND,
    OTHER
  }
}
