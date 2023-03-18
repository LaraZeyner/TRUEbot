package de.zahrie.trues.discord.listener.models;


import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import de.zahrie.trues.api.discord.group.CustomDiscordRole;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.DiscordRoleFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PermissionChangeEvent extends ListenerAdapter {
  @Override
  public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {
    final CustomDiscordRole customRole = DiscordRoleFactory.getCustomRole(event.getRole());
    if (customRole == null) {
      final var group = DiscordGroup.of(event.getRole().getIdLong());
      if (group != null) group.updatePermissions();
    } else if (customRole.isFixed()) customRole.updatePermissions();
  }

  @Override
  public void onGenericPermissionOverride(GenericPermissionOverrideEvent event) {
    if (event.getChannel() instanceof AudioChannel) {
      event.getPermissionOverride().getManager().grant(Permission.VIEW_CHANNEL).queue();
    }

    if (event.getRole() == null) return;
    final DiscordChannel discordChannel = DiscordChannelFactory.getDiscordChannel(event.getChannel());
    if (!discordChannel.getType().isFixed()) return;
    if (discordChannel.updatePermission(event.getRole())) return;
    if (event instanceof PermissionOverrideDeleteEvent) return;
    event.getPermissionOverride().delete().queue();
  }
}
