package de.zahrie.trues.api.discord.command.slash;

import java.util.List;

import de.zahrie.trues.discord.command.CommandRegisterer;
import de.zahrie.trues.api.discord.util.Nunu;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashHandler extends ListenerAdapter {
  private static final List<SlashCommand> commands = new CommandRegisterer().register();

  public static void handleCommands() {
    Nunu.getInstance().getClient().updateCommands().addCommands(
        commands.stream().map(SlashCommand::commandData).toList()
    ).queue();
  }

  @Override
  public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    for (SlashCommand slashCommand : commands) {
      if (slashCommand.getName().equals(event.getName())) {
        slashCommand.handleCommand(event);
        return;
      }
    }
    event.reply("Dieser Command wurde nicht gefunden!").queue();
  }

  @Override
  public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
    commands.stream().filter(slashCommand -> slashCommand.getName().equals(event.getName())).findFirst().ifPresent(slashCommand -> slashCommand.handleAutoCompletion(event));
  }
}
