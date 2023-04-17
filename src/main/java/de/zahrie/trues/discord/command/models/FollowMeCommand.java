package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.event.models.VoiceEvent;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "followme", descripion = "Nutzer verschieben", perm = @Perm(PermissionRole.ORGA_MEMBER), options =
@Option(name = "type", description = "Typ des Folgens", choices = {"Alle", "Team"}))
public class FollowMeCommand extends SlashCommand {

  @Override
  @Msg(value = "Alle ausgewählten Nutzer werden dir folgen.", error = "Dieser Channel konnte nicht gefunden werden.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final GuildVoiceState voiceState = getInvokingMember().getVoiceState();
    if (voiceState == null) return reply("Du bist in keinem Voicechannel");

    final AudioChannelUnion currentChannel = voiceState.getChannel();
    if (currentChannel == null) return reply("Du bist in keinem gültigen Voicechannel");

    VoiceEvent.usersToFollow.put(getInvoker(), find("type").toEnum(FollowType.class, FollowType.ALLE));
    return sendMessage();
  }

  public enum FollowType {
    ALLE,
    TEAM
  }
}
