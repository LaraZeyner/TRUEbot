package de.zahrie.trues.discord.listener.models;


import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Events, die sich auf Member beziehen
 * @see net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
 * @see GuildMemberJoinEvent
 * @see net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
 * @see net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
 * @see net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent
 * @see net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent
 * @see net.dv8tion.jda.api.events.guild.member.update.GenericGuildMemberUpdateEvent
 * @see net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent
 * @see net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent
 * @see GuildMemberUpdateNicknameEvent
 * @see net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent
 * @see net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent
 */
public class MemberEvent extends ListenerAdapter {

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    final var user = new DiscordUser();
    user.setDiscordId(event.getMember().getIdLong());
    user.setMention(event.getMember().getAsMention());
    Database.save(user);

    // TODO send Hello-message
  }

  @Override
  public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
    final DiscordUser user = DiscordUserFactory.getDiscordUser(event.getMember());
    user.setMention(event.getMember().getAsMention());
    Database.save(user);
  }
}
