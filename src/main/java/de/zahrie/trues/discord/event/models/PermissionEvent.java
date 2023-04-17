package de.zahrie.trues.discord.event.models;


import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Events, die sich auf Permissions beziehen
 * @see GenericPermissionOverrideEvent
 * @see net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent
 * @see PermissionOverrideDeleteEvent
 * @see net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent
 */
@ExtensionMethod(DiscordChannelFactory.class)
public class PermissionEvent extends ListenerAdapter {
  @Override
  public void onGenericPermissionOverride(GenericPermissionOverrideEvent event) {
    if (event.getChannel() instanceof AudioChannel) {
      event.getPermissionOverride().getManager().grant(Permission.VIEW_CHANNEL).queue();
    }

    if (event.getRole() == null) return;
    final DiscordChannel discordChannel = event.getChannel().getDiscordChannel();
    if (!discordChannel.getPermissionType().isFixed()) return;
    if (discordChannel.updatePermission(event.getRole())) return;
    if (event instanceof PermissionOverrideDeleteEvent) return;
    event.getPermissionOverride().delete().queue();
  }
}
