package de.zahrie.trues.api.discord.group;

import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.Nullable;

public class DiscordRoleFactory {
  /**
   * Erhalte eine CustomGroup. Ist sie nicht vorhanden, erstelle sie.
   *
   * @return null indicates {@link DiscordGroup}
   */
  @Nullable
  public static CustomDiscordGroup getCustomGroup(Role role) {
    final CustomDiscordGroup customGroup = determineCustomGroup(role);
    if (customGroup == null) {
      final var group = DiscordGroup.of(role);
      if (group == null) return createCustomGroup(role);
    }
    return customGroup;
  }

  private static CustomDiscordGroup determineCustomGroup(Role role) {
    return QueryBuilder.hql(CustomDiscordGroup.class, "FROM CustomDiscordGroup WHERE discordId = " + role.getIdLong()).single();
  }

  private static CustomDiscordGroup createCustomGroup(Role role) {
    final var customGroup = CustomDiscordGroup.build(role.getIdLong(), role.getName(), GroupType.PINGABLE, false);
    role.getManager().setMentionable(true).setPermissions().queue();
    Database.insertAndCommit(customGroup);
    return customGroup;
  }

  /**
   * Entferne CustomGroup ohne sie zu erstellen
   */
  public static void removeCustomGroup(Role role) {
    final CustomDiscordGroup customGroup = determineCustomGroup(role);
    if (customGroup != null) Database.removeAndCommit(customGroup);
  }
}
