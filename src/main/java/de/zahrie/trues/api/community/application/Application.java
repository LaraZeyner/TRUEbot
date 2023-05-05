package de.zahrie.trues.api.community.application;

import java.io.Serial;
import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("application")
public class Application implements Entity<Application> {
  @Serial
  private static final long serialVersionUID = 8214282036590463912L;

  private int id;
  private final DiscordUser user;
  private TeamRole role;

  private final TeamPosition position;
  private LocalDateTime appTimestamp = LocalDateTime.now();
  /**
   * Kann drei Werte annehmen <br>
   * <code>true</code> = warte auf Vorstellungsgespräch <br>
   * <code>false</code> = Vorstellungsgespräch abgehalten <br>
   * <code>null</code> = abgelehnt
   */
  private Boolean waiting = true;

  private String appNotes;

  public Application(DiscordUser user, TeamRole role, TeamPosition position) {
    this.user = user;
    this.role = role;
    this.position = position;
  }

  public Application(DiscordUser user, TeamRole role, TeamPosition position, Boolean waiting, String appNotes) {
    this.user = user;
    this.role = role;
    this.position = position;
    this.waiting = waiting;
    this.appNotes = appNotes;
  }

  public Application(int id, DiscordUser user, TeamRole role, TeamPosition position, LocalDateTime appTimestamp, Boolean waiting, String appNotes) {
    this.id = id;
    this.user = user;
    this.role = role;
    this.position = position;
    this.appTimestamp = appTimestamp;
    this.waiting = waiting;
    this.appNotes = appNotes;
  }

  @Override
  public String toString() {
    return role.name() + " - " + position.name() + "\n" + appNotes;
  }

  public static Application get(Object[] objects) {
    return new Application(
        (int) objects[0],
        new Query<DiscordUser>().entity(objects[1]),
        new SQLEnum<TeamRole>().of(objects[2]),
        new SQLEnum<TeamPosition>().of(objects[3]),
        (LocalDateTime) objects[4],
        (Boolean) objects[5],
        (String) objects[6]
    );
  }

  @Override
  public Application create() {
    return new Query<Application>()
        .key("discord_user", user).key("position", position)
        .col("lineup_role", role).col("app_timestamp", appTimestamp).col("waiting", waiting).col("app_notes", appNotes)
        .insert(this);
  }
}
