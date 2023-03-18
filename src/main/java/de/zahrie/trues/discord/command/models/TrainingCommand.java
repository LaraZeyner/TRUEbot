
package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.discord.Nunu;
import de.zahrie.trues.models.community.application.OrgaMember;
import de.zahrie.trues.models.community.application.OrgaMemberFactory;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "training", descripion = "Nutzer verschieben", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class TrainingCommand extends SlashCommand {
  @Override
  @Msg(value = "Alle Nutzer wurden in den Channel **{}** verschoben.", error = "Dieser Channel konnte nicht gefunden werden.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final DiscordMember discordMember = getInvoker();
    final OrgaMember currentTeam = OrgaMemberFactory.getMostImportantTeam(discordMember);
    final DiscordChannel channel = currentTeam.getOrgaTeam().getVoice();
    final VoiceChannel newChannel = Nunu.DiscordChannel.getVoiceChannel(channel.getDiscordId());
    if (newChannel == null) return errorMessage();
    final GuildVoiceState voiceState = getInvokingMember().getVoiceState();
    if (voiceState == null) return reply("Du bist in keinem Voicechannel");
    final AudioChannelUnion currentChannel = voiceState.getChannel();
    if (currentChannel == null) return reply("Du bist in keinem gültigen Voicechannel");
    currentChannel.getMembers().forEach(member -> Nunu.DiscordChannel.move(member, newChannel));
    return true;
  }
}
