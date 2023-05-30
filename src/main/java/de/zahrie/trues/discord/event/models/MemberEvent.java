package de.zahrie.trues.discord.event.models;


import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.Settings;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Events, die sich auf Member beziehen
 *
 * @see net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
 * @see GuildMemberJoinEvent
 * @see GuildMemberRemoveEvent
 * @see GuildMemberRoleAddEvent
 * @see GuildMemberRoleRemoveEvent
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
    final DiscordUser user = DiscordUserFactory.createDiscordUser(event.getMember());

    final String settings = Arrays.stream(Settings.RegistrationAction.values()).map(registrationAction -> registrationAction.name().toLowerCase()).collect(Collectors.joining(", "));
    event.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("""
        Herzlich Willkommen auf dem Discord von **TRUEsports**.
        _Ich bin dein persönlicher Begleiter und werde deine Fragen beantworten. Sollte also etwas unklar sein schreibe mir gerne eine Frage._
        Bedenke, dass der Bot aktuell noch in Entwicklung ist. Somit sind einige Funktionen nicht nutzbar.
                
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
    user.setNickname(event.getMember().getEffectiveName());
  }

  @Override
  public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
    event.getRoles().stream().map(DiscordGroup::of).filter(Objects::nonNull)
        .forEach(group -> DiscordUserFactory.getDiscordUser(event.getMember()).addGroup(group));
  }

  @Override
  public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
    event.getRoles().stream().map(DiscordGroup::of).filter(Objects::nonNull)
        .forEach(group -> DiscordUserFactory.getDiscordUser(event.getMember()).removeGroup(group));
  }
}
