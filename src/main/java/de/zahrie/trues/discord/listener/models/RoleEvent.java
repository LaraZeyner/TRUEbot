package de.zahrie.trues.discord.listener.models;


import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.group.CustomDiscordGroup;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.DiscordRoleFactory;
import de.zahrie.trues.database.Database;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Events, die sich auf Rollen beziehen
 * @see net.dv8tion.jda.api.events.role.GenericRoleEvent
 * @see RoleCreateEvent
 * @see RoleDeleteEvent
 * @see net.dv8tion.jda.api.events.role.update.GenericRoleUpdateEvent
 * @see net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent
 * @see net.dv8tion.jda.api.events.role.update.RoleUpdateHoistedEvent
 * @see net.dv8tion.jda.api.events.role.update.RoleUpdateIconEvent
 * @see net.dv8tion.jda.api.events.role.update.RoleUpdateMentionableEvent
 * @see RoleUpdateNameEvent
 * @see RoleUpdatePermissionsEvent
 * @see net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent
 */
@ExtensionMethod({DiscordChannelFactory.class, DiscordRoleFactory.class})
public class RoleEvent extends ListenerAdapter {
  @Override
  public void onRoleCreate(RoleCreateEvent event) {
    event.getRole().getCustomGroup();
  }

  @Override
  public void onRoleDelete(RoleDeleteEvent event) {
    event.getRole().removeCustomGroup();
  }

  @Override
  public void onRoleUpdateName(RoleUpdateNameEvent event) {
    final CustomDiscordGroup customRole = event.getRole().getCustomGroup();
    if (customRole == null) {
      final var group = DiscordGroup.of(event.getRole());
      event.getRole().getManager().setName(group.getName()).queue();
    } else {
      customRole.setName(event.getNewName());
      Database.save(customRole);
    }
  }

  @Override
  public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {
    final CustomDiscordGroup customRole = event.getRole().getCustomGroup();
    if (customRole == null) {
      final var group = DiscordGroup.of(event.getRole());
      group.updatePermissions();
    } else if (customRole.isFixed()) customRole.updatePermissions();
  }
}
