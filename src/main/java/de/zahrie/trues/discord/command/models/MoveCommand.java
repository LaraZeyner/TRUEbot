package de.zahrie.trues.discord.command.models;

import java.util.Objects;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.Nunu;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "move", descripion = "Nutzer verschieben", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "channel", description = "Channelname", type = OptionType.CHANNEL)
})
public class MoveCommand extends SlashCommand {
  @Override
  @Msg(value = "Alle Nutzer wurden in den Channel **{}** verschoben.", error = "Du hast keinen Zugriff auf diesen Channel")
  public boolean execute(SlashCommandInteractionEvent event) {
    final Member invoker = getInvokingMember();
    final AudioChannel currentChannel = Nunu.DiscordChannel.getChannel(invoker);
    if (currentChannel == null) return reply("Du bist in keinem gültigen Voicechannel");
    final AudioChannel newChannel = Objects.requireNonNull(event.getOption("channel")).getAsChannel().asAudioChannel();
    boolean success = send(invoker.getPermissions(newChannel).contains(Permission.VOICE_CONNECT), newChannel.getName());
    if (success) {
      currentChannel.getMembers().forEach(member -> Nunu.DiscordChannel.move(member, newChannel));
    }
    return success;
  }
}
