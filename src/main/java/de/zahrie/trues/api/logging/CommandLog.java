package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(value = "orga_log", department = "command")
public class CommandLog implements Entity<CommandLog>, OrgaLog {
  @Serial
  private static final long serialVersionUID = -1495547582387523247L;

  private int id;
  private final LocalDateTime timestamp;
  private final DiscordUser invoker;
  private final String commandName;
  private final String details;

  public static CommandLog get(Object[] objects) {
    return new CommandLog(
        (int) objects[0],
        (LocalDateTime) objects[2],
        new Query<DiscordUser>().entity(objects[3]),
        (String) objects[5],
        (String) objects[6]
    );
  }

  @Override
  public CommandLog create() {
    return new Query<CommandLog>().key("department", "command")
        .key("log_time", getTimestamp()).key("invoker", getInvoker())
        .key("details", getDetails()).key("action", getCommandName())
        .insert(this);
  }
}
