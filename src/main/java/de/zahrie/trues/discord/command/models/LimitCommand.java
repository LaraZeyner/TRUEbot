package de.zahrie.trues.discord.command.models;

import java.util.List;

import de.zahrie.trues.api.datatypes.collections.SortedList;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.util.Nunu;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "limit", descripion = "Channel begrenzen", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "nutzer", description = "Anzahl Nutzer", required = false, type = OptionType.INTEGER)
})
public class LimitCommand extends SlashCommand {
  public static List<Long> limitedChannels = SortedList.of();

  @Override
  @Msg(value = "Der Channel wurde auf **{}** Nutzer limitiert.", error = "Der gib eine gültige Zahl ein.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final AudioChannel channel = Nunu.DiscordChannel.getVoiceChannel(getInvokingMember());
    if (!(channel instanceof VoiceChannel)) return reply("Der Channel konnte nicht gefunden werden.");
    final int defaultUserLimit = channel.getUserLimit() == 0 ? 2 : 0;
    final var amount = find("nutzer").integer(defaultUserLimit);
    final boolean success = limitTo((AudioChannelUnion) channel, amount);
    return amount == 0 ? reply("Die Limitierung wurde zurückgesetzt.") : send(success, amount);
  }

  private static boolean limitTo(AudioChannelUnion voiceChannel, int amount) {
    if (amount < 0 || amount > 99) return false;

    if (amount != 0) limitedChannels.add(voiceChannel.getIdLong());
    if (amount == 0) limitedChannels.remove(voiceChannel.getIdLong());
    voiceChannel.getManager().setUserLimit(amount).queue();
    return true;
  }


  public static void handleChannelLeft(GuildVoiceUpdateEvent event) {
    if (event.getChannelLeft() == null) return;

    if (limitedChannels.contains(event.getChannelLeft().getIdLong()) && event.getChannelLeft().getMembers().isEmpty()) {
      limitTo(event.getChannelLeft(), 0);
    }
  }
}
