package de.zahrie.trues.dc.listener;


import de.zahrie.trues.models.discord.DiscordUser;
import de.zahrie.trues.util.database.Database;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter {

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    final var user = new DiscordUser();
    user.setDiscordId(event.getMember().getIdLong());
    Database.save(user);

    // TODO send Hello-message
  }

}
