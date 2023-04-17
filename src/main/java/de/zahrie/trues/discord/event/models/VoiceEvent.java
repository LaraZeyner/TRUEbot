package de.zahrie.trues.discord.event.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.discord.command.models.FollowMeCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Events, die sich auf Voicechannel beziehen
 *
 * @see net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceRequestToSpeakEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfDeafenEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceSuppressEvent
 * @see GuildVoiceUpdateEvent
 * @see net.dv8tion.jda.api.events.guild.voice.GuildVoiceVideoEvent
 */
public class VoiceEvent extends ListenerAdapter {

  public static final Map<DiscordUser, FollowMeCommand.FollowType> usersToFollow = new HashMap<>();

  @Override
  public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
    final DiscordUser discordUser = DiscordUserFactory.getDiscordUser(event.getMember());
    discordUser.addSeconds(event.getChannelJoined() != null);

    if (!usersToFollow.containsKey(discordUser)) return;

    usersToFollow.remove(discordUser);
    if (event.getChannelJoined() == null || event.getChannelLeft() == null) return;

    final Membership currentTeam = MembershipFactory.getMostImportantTeam(discordUser);
    final List<DiscordUser> membersOfTeam = currentTeam.getOrgaTeam().getActiveMemberships().stream().map(Membership::getUser).toList();
    final List<Member> validMembers = switch (usersToFollow.get(discordUser)) {
      case ALLE -> event.getChannelLeft().getMembers();
      case TEAM -> event.getChannelLeft().getMembers().stream().filter(member -> membersOfTeam.contains(DiscordUserFactory.getDiscordUser(member))).toList();
    };
    validMembers.forEach(member -> Nunu.DiscordChannel.move(member, event.getChannelJoined()));
    usersToFollow.remove(discordUser);
  }
}
