package de.zahrie.trues.discord.listener.models;


import java.util.Arrays;
import java.util.stream.Collectors;

import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.logging.ServerLog;
import de.zahrie.trues.discord.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Events, die sich auf Member beziehen
 *
 * @see net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
 * @see GuildMemberJoinEvent
 * @see GuildMemberRemoveEvent
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

    final ServerLog serverLog = new ServerLog(user, "", ServerLog.ServerLogAction.SERVER_JOIN);
    Database.save(serverLog);

    final String settings = Arrays.stream(Settings.RegistrationAction.values()).map(registrationAction -> registrationAction.name().toLowerCase()).collect(Collectors.joining(", "));
    user.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("""
        Herzlich Willkommen auf dem Discord von **TRUEsports**.
        _Ich bin dein persönlicher Begleiter und werde deine Fragen beantworten. Sollte also etwas unklar sein schreibe mir gerne eine Frage._
                
        Als nächstes solltest du dich registrieren. Dies kannst du mit
        `lol_name: DiesistmeinName`
        machen. Weiterhin gibt es noch weitere Einstellungsmöglichkeiten: `
        """ + settings + """
        `
        
        Unsere Regeln sowie Rollenauswahl findest du auf unserem Server. Viel Spaß!
        """).queue());
  }

  @Override
  public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
    final DiscordUser user = DiscordUserFactory.getDiscordUser(event.getMember());
    user.setMention(event.getMember().getAsMention());
    Database.save(user);
  }

  @Override
  public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
    final Member member = event.getMember();
    if (member != null) {
      final DiscordUser user = DiscordUserFactory.getDiscordUser(member);
      final ServerLog serverLog = new ServerLog(user, "", ServerLog.ServerLogAction.SERVER_LEAVE);
      Database.save(serverLog);
    }
  }
}
