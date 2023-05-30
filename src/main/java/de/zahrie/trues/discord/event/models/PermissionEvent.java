package de.zahrie.trues.discord.event.models;


import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Events, die sich auf Permissions beziehen
 * @see GenericPermissionOverrideEvent
 * @see net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent
 * @see net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent
 * @see net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent
 */
@ExtensionMethod(DiscordChannelFactory.class)
public class PermissionEvent extends ListenerAdapter {
  @Override
  public void onGenericPermissionOverride(@NonNull GenericPermissionOverrideEvent event) {
    if (event.getChannel() instanceof AudioChannel && event.getPermissionOverride().getDenied().contains(Permission.VIEW_CHANNEL)) {
      event.getPermissionOverride().getManager().grant(Permission.VIEW_CHANNEL).queue();
    }
  }
}
