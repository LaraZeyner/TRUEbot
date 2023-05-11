package de.zahrie.trues.discord.event.models;

import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.logging.ServerLog;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.update.GenericGuildUpdateEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.stage.GenericStageInstanceEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.NotNull;

public class EventLogger extends ListenerAdapter {
  @Override
  public void onGenericEvent(@NotNull GenericEvent event) {
    if (event instanceof MessageDeleteEvent ||
        event instanceof MessageUpdateEvent ||
        event instanceof GuildMemberJoinEvent ||
        event instanceof GuildMemberRemoveEvent ||
        event instanceof GuildBanEvent ||
        event instanceof GuildUnbanEvent ||
        event instanceof GenericPermissionOverrideEvent ||
        event instanceof GenericStageInstanceEvent ||
        event instanceof GenericGuildUpdateEvent ||
        event instanceof GenericChannelEvent ||
        event instanceof GenericRoleEvent ||
        event instanceof GenericInteractionCreateEvent) {
      final Member guildMember = determineGuildMember(event);
      if (guildMember != null && guildMember.getUser().equals(Nunu.getInstance().getClient().getSelfUser())) return;
      final DiscordUser discordUser = guildMember == null ? null : DiscordUserFactory.getDiscordUser(guildMember);
      final String details = determineDetails(event);
      new ServerLog(discordUser, details, ServerLog.ServerLogAction.fromClass(event.getClass())).forceCreate();
    }
  }

  private String determineDetails(GenericEvent event) {
    if (event instanceof GenericMessageEvent messageDeleteEvent) {
      final String[] msg = new String[1];
      messageDeleteEvent.getChannel().retrieveMessageById(messageDeleteEvent.getMessageId()).queue(message -> msg[0] = message.getContentDisplay());
      return msg[0];
    }
    if (event instanceof CommandInteraction guildBanEvent) return guildBanEvent.getName() + " " + guildBanEvent.getCommandString();
    return event.getClass().getSimpleName();
  }

  private Member determineGuildMember(GenericEvent event) {
    if (event instanceof GuildMemberJoinEvent guildBanEvent) return guildBanEvent.getMember();
    if (event instanceof GuildMemberRemoveEvent guildBanEvent) return guildBanEvent.getMember();
    if (event instanceof GuildBanEvent guildBanEvent) return Nunu.DcMember.getMember(guildBanEvent.getUser());
    if (event instanceof GuildUnbanEvent guildBanEvent) return Nunu.DcMember.getMember(guildBanEvent.getUser());
    if (event instanceof GenericInteractionCreateEvent guildBanEvent) return Nunu.DcMember.getMember(guildBanEvent.getUser());
    return null;
  }
}
