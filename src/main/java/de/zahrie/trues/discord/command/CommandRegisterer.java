package de.zahrie.trues.discord.command;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.discord.command.models.MoveCommand;
import de.zahrie.trues.discord.command.models.RegisterCommand;
import de.zahrie.trues.discord.command.models.TrainingCommand;

public class CommandRegisterer implements Registerer<List<SlashCommand>> {
  @Override
  public List<SlashCommand> register() {
    return List.of(
        new MoveCommand(),
        new RegisterCommand(),
        new TrainingCommand()
    );
  }
}
