package de.zahrie.trues.discord.listener.models;


import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.database.Database;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter {

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    final var user = new DiscordMember();
    user.setDiscordId(event.getMember().getIdLong());
    user.setMention(event.getMember().getAsMention());
    Database.save(user);

    // TODO send Hello-message
  }
}
