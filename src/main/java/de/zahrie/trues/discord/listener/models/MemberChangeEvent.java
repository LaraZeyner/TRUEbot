package de.zahrie.trues.discord.listener.models;


import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.api.discord.member.DiscordMemberFactory;
import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberChangeEvent extends ListenerAdapter {
  @Override
  public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
    final DiscordMember member = DiscordMemberFactory.getMember(event.getMember());
    member.setMention(event.getMember().getAsMention());
    Database.save(member);
  }
}
