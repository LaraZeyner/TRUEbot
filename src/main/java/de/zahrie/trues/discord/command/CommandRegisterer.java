package de.zahrie.trues.discord.command;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.discord.command.models.BewerbenCommand;
import de.zahrie.trues.discord.command.models.BewerbungenCommand;
import de.zahrie.trues.discord.command.models.LimitCommand;
import de.zahrie.trues.discord.command.models.MoveCommand;
import de.zahrie.trues.discord.command.models.RegisterCommand;
import de.zahrie.trues.discord.command.models.TrainingCommand;
import de.zahrie.trues.discord.command.models.channel.ChannelCommand;
import de.zahrie.trues.discord.command.models.channel.ChannelCreateCommand;
import de.zahrie.trues.discord.command.models.channel.ChannelEditCommand;

public class CommandRegisterer implements Registerer<List<SlashCommand>> {
  @Override
  public List<SlashCommand> register() {
    return List.of(
        new BewerbenCommand(),
        new BewerbungenCommand(),
        new ChannelCommand(new ChannelCreateCommand(), new ChannelEditCommand()),
        new LimitCommand(),
        new MoveCommand(),
        new RegisterCommand(),
        new TrainingCommand()
    );
  }
}
