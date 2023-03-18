package de.zahrie.trues.api.riot.matchhistory.performance;

import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.Roleable;
import de.zahrie.trues.discord.Nunu;
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
  private final String displayName;

  public Role getRole() {
    return Nunu.guild.getRoleById(getDiscordId());
  }

  public Role getHelperRole() {
    return Nunu.guild.getRoleById(getDiscordIdHelp());
  }

  public long getDiscordId() {
    return DiscordGroup.valueOf(name()).getDiscordId();
  }

  public Long getDiscordIdHelp() {
    return DiscordGroup.valueOf(name() + "_HELP").getDiscordId();
  }
}
