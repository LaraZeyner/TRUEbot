package de.zahrie.trues.api.discord.listener;


import de.zahrie.trues.models.discord.member.DiscordMember;
import de.zahrie.trues.util.database.Database;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter {

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    final var user = new DiscordMember();
    user.setDiscordId(event.getMember().getIdLong());
    Database.save(user);

    // TODO send Hello-message
  }

}
