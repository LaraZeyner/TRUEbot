package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.util.io.log.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(value = "orga_log", department = "custom")
public class CustomLog implements Entity<CustomLog>, OrgaLog {
  @Serial
  private static final long serialVersionUID = 2670059234239183597L;

  private int id;
  private final LocalDateTime timestamp;
  private final String details;
  private final Level level;

  public static CustomLog get(Object[] objects) {
    return new CustomLog(
        (int) objects[0],
        (LocalDateTime) objects[2],
        (String) objects[5],
        new SQLEnum<Level>().of(objects[6])
    );
  }

  @Override
  public CustomLog create() {
    return new Query<CustomLog>().key("department", "custom")
        .key("log_time", LocalDateTime.now()).key("details", getDetails()).key("action", level)
        .insert(this);
  }
}
