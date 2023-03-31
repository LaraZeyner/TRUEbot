package de.zahrie.trues.discord.command;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.discord.command.models.BetCommand;
import de.zahrie.trues.discord.command.models.BewerbenCommand;
import de.zahrie.trues.discord.command.models.LimitCommand;
import de.zahrie.trues.discord.command.models.LineupCommand;
import de.zahrie.trues.discord.command.models.MoveCommand;
import de.zahrie.trues.discord.command.models.RegisterCommand;
import de.zahrie.trues.discord.command.models.ScoutCommand;
import de.zahrie.trues.discord.command.models.ScrimCommand;
import de.zahrie.trues.discord.command.models.channel.ChannelCommand;
import de.zahrie.trues.discord.command.models.leaderboard.StatsCommand;
import de.zahrie.trues.discord.command.models.team.TeamCommand;
import de.zahrie.trues.discord.command.models.training.TrainingCommand;
import de.zahrie.trues.discord.command.models.tryout.TryoutCommand;

public class CommandRegisterer implements Registerer<List<SlashCommand>> {
  @Override
  public List<SlashCommand> register() {
    return List.of(
        new BetCommand(),
        new BewerbenCommand(),
        new ChannelCommand(),
        new LimitCommand(),
        new LineupCommand(),
        new MoveCommand(),
        new RegisterCommand(),
        new ScoutCommand(),
        new ScrimCommand(),
        new StatsCommand(),
        new TeamCommand(),
        new TrainingCommand(),
        new TryoutCommand()
    );
  }
}
