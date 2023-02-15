package de.zahrie.trues.api.discord.util.cmd;

import java.util.List;

import de.zahrie.trues.api.discord.Nunu;
import de.zahrie.trues.api.discord.command.TestCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 10.02.2023 for TRUEbot
 */
public class CommandHandler extends ListenerAdapter {
  private static final List<SlashCommand> commands = List.of(new TestCommand());

  public static void handleCommands() {
    Nunu.client.updateCommands().addCommands(
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
    for (SlashCommand slashCommand : commands) {
      if (slashCommand.getName().equals(event.getName())) {
        slashCommand.handleAutoCompletion(event);
        return;
      }
    }
  }
}
