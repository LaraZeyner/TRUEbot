package de.zahrie.trues.discord.command;

import java.util.List;

import de.zahrie.trues.api.Registerer;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.discord.command.models.BetCommand;
import de.zahrie.trues.discord.command.models.BewerbenCommand;
import de.zahrie.trues.discord.command.models.FollowMeCommand;
import de.zahrie.trues.discord.command.models.LimitCommand;
import de.zahrie.trues.discord.command.models.LineupCommand;
import de.zahrie.trues.discord.command.models.MoveCommand;
import de.zahrie.trues.discord.command.models.ScoutCommand;
import de.zahrie.trues.discord.command.models.ScrimCommand;
import de.zahrie.trues.discord.command.models.SettingsCommand;
import de.zahrie.trues.discord.command.models.TrainingCommand;
import de.zahrie.trues.discord.command.models.channel.ChannelCreateCommand;
import de.zahrie.trues.discord.command.models.channel.ChannelEditCommand;
import de.zahrie.trues.discord.command.models.leaderboard.GlobalStatsCommand;
import de.zahrie.trues.discord.command.models.leaderboard.TeamStatsCommand;
import de.zahrie.trues.discord.command.models.team.TeamCreateCommand;
import de.zahrie.trues.discord.command.models.team.TeamFollowCommand;
import de.zahrie.trues.discord.command.models.team.TeamLinkCommand;
import de.zahrie.trues.discord.command.models.tryout.TryoutAcceptCommand;
import de.zahrie.trues.discord.command.models.tryout.TryoutAddCommand;
import de.zahrie.trues.discord.command.models.tryout.TryoutCustomCommand;
import de.zahrie.trues.discord.command.models.tryout.TryoutListCommand;

public class CommandRegisterer implements Registerer<List<SlashCommand>> {
  @Override
  public List<SlashCommand> register() {
    return List.of(
        new ChannelCreateCommand(),
        new ChannelEditCommand(),

        new GlobalStatsCommand(),
        new TeamStatsCommand(),

        new TeamCreateCommand(),
        new TeamFollowCommand(),
        new TeamLinkCommand(),

        new TryoutAcceptCommand(),
        new TryoutAddCommand(),
        new TryoutCustomCommand(),
        new TryoutListCommand(),

        new BetCommand(),
        new BewerbenCommand(),
        //new ChannelCommand(),
        new FollowMeCommand(),
        new LimitCommand(),
        new LineupCommand(),
        new MoveCommand(),
        new SettingsCommand(),
        new ScoutCommand(),
        new ScrimCommand(),
        //new StatsCommand(),
        //new TeamCommand(),
        new TrainingCommand()
        //new TryoutCommand()
    );
  }
}
