package de.zahrie.trues.discord.listener.models;

import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.channel.DiscordChannelFactory;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelEvent extends ListenerAdapter {

  @Override
  public void onChannelCreate(ChannelCreateEvent event) {
    final GuildChannel channel = event.getChannel().asGuildChannel();
    final DiscordChannel discordChannel = DiscordChannelFactory.createChannel(channel);
    if (discordChannel.getType().isFixed()) {
      discordChannel.updatePermissions();
    }
  }
}
