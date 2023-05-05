package de.zahrie.trues.api.discord.user;

import java.io.Serial;
import java.time.Duration;
import java.time.LocalDateTime;

import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.datatypes.calendar.TimeRange;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.GroupAssignReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Table("discord_user_group")
public class DiscordUserGroup implements Entity<DiscordUserGroup> {
  @Serial
  private static final long serialVersionUID = 7171651659881865861L;

  private int id; // discord_user_group_id
  private final DiscordUser user; // discord_user
  private final DiscordGroup discordGroup; // discord_group
  private TimeRange range; // assign_time, permission_end
  private final GroupAssignReason reason; // reason
  private boolean active = false; // active

  public DiscordUserGroup(DiscordUser user, DiscordGroup discordGroup, TimeRange range) {
    this.user = user;
    this.discordGroup = discordGroup;
    this.range = range;
    this.reason = GroupAssignReason.ADD;
  }

  public static DiscordUserGroup get(Object[] objects) {
    return new DiscordUserGroup(
        (int) objects[0],
        new Query<DiscordUser>().entity(objects[1]),
        new SQLEnum<DiscordGroup>().of(objects[2]),
        new TimeRange((LocalDateTime) objects[3], (LocalDateTime) objects[4]),
        new SQLEnum<GroupAssignReason>().of(objects[5]),
        (boolean) objects[6]
    );
  }

  @Override
  public DiscordUserGroup create() {
    return new Query<DiscordUserGroup>().key("discord_user", user).key("discord_group", discordGroup)
        .col("assign_time", range.getStartTime()).col("permission_end", range.getEndTime()).col("reason", reason).col("active", active)
        .insert(this);
  }

  public void end() {
    this.active = false;
    this.range = new TimeRange(range.getStartTime(), LocalDateTime.now());
    new Query<DiscordUserGroup>().col("active", active).col("assign_time", range.getStartTime()).col("permission_end", range.getEndTime())
        .update(id);
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setActive(boolean active) {
    this.active = active;
    new Query<DiscordUserGroup>().col("active", active).update(id);
  }

  public void updateTimeRange(LocalDateTime start, int days) {
    final LocalDateTime end = range.getEndTime();
    final var timeRange = new TimeRange(start, Duration.ofDays(days));
    if (timeRange.getEndTime().isAfter(end)) {
      this.range = timeRange;
      new Query<DiscordUserGroup>().col("assign_time", range.getStartTime()).col("permission_end", range.getEndTime()).update(id);
    }
  }
}
