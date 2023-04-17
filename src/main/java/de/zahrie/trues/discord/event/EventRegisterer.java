package de.zahrie.trues.discord.event;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.discord.event.models.ChannelEvent;
import de.zahrie.trues.discord.event.models.MemberEvent;
import de.zahrie.trues.discord.event.models.MessageEvent;
import de.zahrie.trues.discord.event.models.PermissionEvent;
import de.zahrie.trues.discord.event.models.RoleEvent;
import de.zahrie.trues.discord.event.models.VoiceEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @see net.dv8tion.jda.api.events.channel ChannelEvent
 * @see net.dv8tion.jda.api.events.emoji
 * @see net.dv8tion.jda.api.events.guild
 * @see net.dv8tion.jda.api.events.guild.invite
 * @see net.dv8tion.jda.api.events.guild.member MemberEvent
 * @see net.dv8tion.jda.api.events.guild.override PermissionEvent
 * @see net.dv8tion.jda.api.events.guild.scheduledevent
 * @see net.dv8tion.jda.api.events.guild.update
 * @see net.dv8tion.jda.api.events.guild.voice VoiceEvent
 * @see net.dv8tion.jda.api.events.interaction
 * @see net.dv8tion.jda.api.events.message MessageEvent
 * @see net.dv8tion.jda.api.events.role RoleEvent
 * @see net.dv8tion.jda.api.events.self
 * @see net.dv8tion.jda.api.events.session
 * @see net.dv8tion.jda.api.events.stage
 * @see net.dv8tion.jda.api.events.sticker
 * @see net.dv8tion.jda.api.events.thread
 * @see net.dv8tion.jda.api.events.user
 */
public class EventRegisterer implements Registerer<List<ListenerAdapter>> {
  @Override
  public List<ListenerAdapter> register() {
    return List.of(
        new ChannelEvent(), // 11 Events
        new MemberEvent(), // 12 Events
        new PermissionEvent(), // 4 Events
        new VoiceEvent(), // 12 Events
        new MessageEvent(), // 11 Events
        new RoleEvent() // 11 Events
    );
  }
}
