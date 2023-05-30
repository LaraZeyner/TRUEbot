package de.zahrie.trues.discord.command.models.deprecated;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.command.models.TryoutAcceptCommand;
import de.zahrie.trues.discord.command.models.TryoutAddCommand;
import de.zahrie.trues.discord.command.models.TryoutCustomCommand;
import de.zahrie.trues.discord.command.models.TryoutListCommand;
import de.zahrie.trues.util.io.SelectionQueries;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "tryout", descripion = "Ein Bewerbungsgespräch führen", perm = @Perm(PermissionRole.TEAM_BUILDING), options = {
    @Option(name = "bewerbung", description = "Bewerbungsdings", completion = SelectionQueries.PENDING_APPLICATIONS),
    @Option(name = "zeitpunkt", description = "Zeitpunkt", type = OptionType.STRING)
})
@Deprecated
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
