
package de.zahrie.trues.discord.command.models.training;

import de.zahrie.trues.api.community.orgateam.TeamChannelType;
import de.zahrie.trues.api.discord.channel.DiscordChannel;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.community.member.Membership;
import de.zahrie.trues.api.community.member.MembershipFactory;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "start", descripion = "Nutzer verschieben", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class TrainingStartCommand extends SlashCommand {
  public TrainingStartCommand(SlashCommand... commands) {
    super(commands);
  }

  @Override
  @Msg(value = "Alle Nutzer wurden in den Channel **{}** verschoben.", error = "Dieser Channel konnte nicht gefunden werden.")
  public boolean execute(SlashCommandInteractionEvent event) {
    //TODO (Abgie) 21.03.2023: modes
    final DiscordUser discordUser = getInvoker();
    final Membership currentTeam = MembershipFactory.getMostImportantTeam(discordUser);
    final DiscordChannel channel = currentTeam.getOrgaTeam().getTeamChannels().stream().filter(teamChannel -> teamChannel.getTeamChannelType().equals(TeamChannelType.PRACTICE)).findFirst().orElse(null);
    if (channel == null) return errorMessage();

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
