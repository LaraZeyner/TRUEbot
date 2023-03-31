package de.zahrie.trues.discord.command.models.tryout;

import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.discord.util.Nunu;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "tryout", descripion = "Ein Bewerbungsgespräch führen", perm = @Perm(PermissionRole.TEAM_BUILDING), options = {
    @Option(name = "bewerbung", description = "Bewerbungsdings", completion = "Application.pending"),
    @Option(name = "zeitpunkt", description = "Zeitpunkt", type = OptionType.STRING)
})
@ExtensionMethod({StringExtention.class, Nunu.DiscordMessager.class})
public class TryoutCommand extends SlashCommand {

  public TryoutCommand() {
    super(
        new TryoutAcceptCommand(),
        new TryoutAddCommand(),
        new TryoutCustomCommand(),
        new TryoutListCommand()
    );
  }

  @Override
  @Msg
  public boolean execute(SlashCommandInteractionEvent event) {
    return true;
  }
}
