package de.zahrie.trues.api.logging;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.database.connector.Listing;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLEnum;
import de.zahrie.trues.api.discord.user.DiscordUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.update.GenericGuildUpdateEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.stage.GenericStageInstanceEvent;

@Getter
@Setter
@Table(value = "orga_log", department = "member")
public class ServerLog extends OrgaLog implements Entity<ServerLog> {
  @Serial
  private static final long serialVersionUID = 5363213064192041859L;

  private final DiscordUser invoker;
  private final DiscordUser target;
  private final ServerLogAction action;

  public ServerLog(LocalDateTime timestamp, String details, DiscordUser invoker, DiscordUser target, ServerLogAction action) {
    super(timestamp, details);
    this.invoker = invoker;
    this.target = target;
    this.action = action;
  }

  public ServerLog(int id, LocalDateTime timestamp, String details, DiscordUser invoker, DiscordUser target, ServerLogAction action) {
    super(id, timestamp, details);
    this.invoker = invoker;
    this.target = target;
    this.action = action;
  }

  public ServerLog(DiscordUser invoker, DiscordUser target, String details, ServerLogAction action) {
    this(LocalDateTime.now(), details, invoker, target, action);
  }

  public ServerLog(DiscordUser target, String details, ServerLogAction action) {
    this(LocalDateTime.now(), details, null, target, action);
  }

  public static ServerLog get(List<Object> objects) {
    return new ServerLog(
        (int) objects.get(0),
        (LocalDateTime) objects.get(2),
        (String) objects.get(5),
        new Query<>(DiscordUser.class).entity(objects.get(3)),
        new Query<>(DiscordUser.class).entity(objects.get(4)),
        new SQLEnum<>(ServerLogAction.class).of(objects.get(6))
    );
  }

  @Override
  public ServerLog create() {
    return new Query<>(ServerLog.class)
        .key("log_time", getTimestamp()).key("target", getTarget()).key("details", getDetails()).key("action", action)
        .insert(this);
  }

  @RequiredArgsConstructor
  @Getter
  @Listing(Listing.ListingType.LOWER)
  public enum ServerLogAction {
    APPLICATION_CREATED(null),
    OTHER(null),

    MESSAGE_DELETED(MessageDeleteEvent.class),
    MESSAGE_UPDATED(MessageUpdateEvent.class),
    SERVER_JOIN(GuildMemberJoinEvent.class),
    SERVER_LEAVE(GuildMemberRemoveEvent.class),
    BAN(GuildBanEvent.class),
    UNBAN(GuildUnbanEvent.class),
    PERMISSION_CHANGE(GenericPermissionOverrideEvent.class),
    STAGE_CHANGE(GenericStageInstanceEvent.class),
    SERVER_CHANGE(GenericGuildUpdateEvent.class),
    CHANNEL_CHANGE(GenericChannelEvent.class),
    ROLE_CHANGE(GenericRoleEvent.class),
    COMMAND(GenericInteractionCreateEvent.class);
    private final Class<? extends GenericEvent> eventClass;

    public static ServerLogAction fromClass(Class<? extends GenericEvent> eventClass) {
      return Arrays.stream(ServerLogAction.values()).filter(action -> action.getEventClass() != null && action.getEventClass().equals(eventClass)).findFirst()
          .orElseGet(() -> Arrays.stream(ServerLogAction.values()).filter(action -> action.getEventClass() != null && action.getEventClass().isAssignableFrom(eventClass)).findFirst().orElse(OTHER));
    }
  }
}
