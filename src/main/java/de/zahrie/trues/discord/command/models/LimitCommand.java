package de.zahrie.trues.discord.command.models;

import java.util.Objects;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.Nunu;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "register", descripion = "Channel begrenzen", perm = @Perm(PermissionRole.REGISTERED), options = {
    @Option(name = "nutzer", description = "Anzahl Nutzer", type = OptionType.NUMBER)
})
public class LimitCommand extends SlashCommand {
  @Override
  @Msg(value = "Der Channel wurde auf **{}** Nutzer limitiert.", error = "Der gib eine gültige Zahl ein.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final var amount = Objects.requireNonNull(event.getOption("nutzer")).getAsInt();
    final AudioChannel channel = Nunu.DiscordChannel.getVoiceChannel(getInvokingMember());
    if (!(channel instanceof VoiceChannel)) return reply("Der Channel konnte nicht gefunden werden.");
    final boolean success = Nunu.DiscordChannel.limitTo((VoiceChannel) channel, amount);
    if (amount == 0) {
      return reply("Die Limitierung wurde zurückgesetzt.");
    }
    return send(success, amount);
  }
}
