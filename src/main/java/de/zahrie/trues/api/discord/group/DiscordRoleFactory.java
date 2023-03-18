package de.zahrie.trues.api.discord.group;

import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.entities.Role;

public class DiscordRoleFactory {
  public static CustomDiscordRole getCustomRole(Role role) {
    return Database.Find.find(CustomDiscordRole.class, new String[]{"discordId"}, new Object[]{role.getIdLong()}, "fromDiscordId");
  }
}
