package de.zahrie.trues.discord.command;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.discord.command.models.BetCommand;
import de.zahrie.trues.discord.command.models.BewerbenCommand;
import de.zahrie.trues.discord.command.models.ChannelCreateCommand;
import de.zahrie.trues.discord.command.models.ChannelEditCommand;
import de.zahrie.trues.discord.command.models.FollowMeCommand;
import de.zahrie.trues.discord.command.models.HelpCommand;
import de.zahrie.trues.discord.command.models.LimitCommand;
import de.zahrie.trues.discord.command.models.LineupCommand;
import de.zahrie.trues.discord.command.models.QuotesCommand;
import de.zahrie.trues.discord.command.models.ScoutCommand;
import de.zahrie.trues.discord.command.models.ScrimCommand;
import de.zahrie.trues.discord.command.models.SettingsCommand;
import de.zahrie.trues.discord.command.models.StatsCommand;
import de.zahrie.trues.discord.command.models.TeamCreateCommand;
import de.zahrie.trues.discord.command.models.TeamFollowCommand;
import de.zahrie.trues.discord.command.models.TeamLinkCommand;
import de.zahrie.trues.discord.command.models.TrainingCommand;
import de.zahrie.trues.discord.command.models.TryoutAcceptCommand;
import de.zahrie.trues.discord.command.models.TryoutAddCommand;
import de.zahrie.trues.discord.command.models.TryoutCustomCommand;
import de.zahrie.trues.discord.command.models.TryoutListCommand;

public class CommandRegisterer implements Registerer<List<SlashCommand>> {
  @Override
  public List<SlashCommand> register() {
    return List.of(
        new BetCommand(),
        new BewerbenCommand(),
        new ChannelCreateCommand(),
        new ChannelEditCommand(),
        new FollowMeCommand(),
        new HelpCommand(),
        new LimitCommand(),
        new LineupCommand(),
        new QuotesCommand(),
        new StatsCommand(),
        new SettingsCommand(),
        new ScoutCommand(),
        new ScrimCommand(),
        new TeamCreateCommand(),
        new TeamFollowCommand(),
        new TeamLinkCommand(),
        new TrainingCommand(),
        new TryoutAcceptCommand(),
        new TryoutAddCommand(),
        new TryoutCustomCommand(),
        new TryoutListCommand()
    );
  }
}
