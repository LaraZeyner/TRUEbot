package de.zahrie.trues.api.riot;

import de.zahrie.trues.api.discord.group.Roleable;
import de.zahrie.trues.discord.Nunu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;

@RequiredArgsConstructor
@Getter
public enum Lane implements Roleable {
  UNKNOWN(null, null),
  TOP(1L, 2L),
  JUNGLE(1L, 2L),
  MIDDLE(1L, 2L),
  BOTTOM(1L, 2L),
  UTILITY(1L, 2L);

  private final Long discordId;
  private final Long discordIdHelp;

  public Role getRole() {
    return Nunu.guild.getRoleById(discordId);
  }
}
