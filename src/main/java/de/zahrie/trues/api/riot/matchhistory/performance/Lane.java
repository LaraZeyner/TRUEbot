package de.zahrie.trues.api.riot.matchhistory.performance;

import java.util.List;

import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.Roleable;
import de.zahrie.trues.api.discord.util.Nunu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;

@RequiredArgsConstructor
@Getter
public enum Lane implements Roleable {
  UNKNOWN("null"),
  TOP("Top"),
  JUNGLE("Jungle"),
  MIDDLE("Middle"),
  BOTTOM("Bottom"),
  UTILITY("Support");

  public static final List<Lane> ITERATE = List.of(TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY);
  private final String displayName;

  public Role getRole() {
    return Nunu.getInstance().getGuild().getRoleById(getDiscordId());
  }

  public Role getHelperRole() {
    return Nunu.getInstance().getGuild().getRoleById(getDiscordIdHelp());
  }

  public long getDiscordId() {
    return DiscordGroup.valueOf(name()).getDiscordId();
  }

  public Long getDiscordIdHelp() {
    return DiscordGroup.valueOf(name() + "_HELP").getDiscordId();
  }

  public static Lane transform(com.merakianalytics.orianna.types.common.Lane lane) {
    return switch (lane) {
      case BOT -> BOTTOM;
      case MID -> MIDDLE;
      case NONE -> UNKNOWN;
      default -> Lane.valueOf(lane.name());
    };
  }
}
