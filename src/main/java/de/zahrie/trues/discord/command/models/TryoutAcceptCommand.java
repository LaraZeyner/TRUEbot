package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "accept", descripion = "Ein Bewerbungsgespräch führen", perm = @Perm(PermissionRole.MANAGEMENT),
    options = @Option(name = "nutzer", description = "Bewerbungsdings", type = OptionType.USER))
@ExtensionMethod(DiscordUserFactory.class)
public class TryoutAcceptCommand extends SlashCommand {
  @Override
  @Msg(value = "Der Tryout ist angenommen.", error = "Der Nutzer wurde nicht gefunden.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final DiscordUser discordUser = find("nutzer").discordUser();
    if (discordUser == null) return errorMessage();

    discordUser.setAcceptedBy(getInvoker());
    return sendMessage();
  }
}
