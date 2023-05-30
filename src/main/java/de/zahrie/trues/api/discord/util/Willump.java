package de.zahrie.trues.api.discord.util;

import java.util.ArrayList;
import java.util.List;

import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.builder.leaderboard.LeaderboardHandler;
import de.zahrie.trues.api.discord.command.InputHandler;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.scheduler.ScheduleManager;
import de.zahrie.trues.discord.command.CommandRegisterer;
import de.zahrie.trues.discord.context.ContextRegisterer;
import de.zahrie.trues.discord.event.EventRegisterer;
import de.zahrie.trues.discord.event.models.EventLogger;
import de.zahrie.trues.util.Connectable;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public class Willump implements Connectable {
  private JDA client;
  private Guild guild;

  private static final List<SlashCommand> commands = new CommandRegisterer().register();
  private static final List<ContextCommand> context = new ContextRegisterer().register();

  public static List<SlashCommand> getCommands() {
    return commands;
  }

  public static List<ContextCommand> getContext() {
    return context;
  }

  public void connect() {
    this.client = new BotConfigurator().run();
    handleEvents();
    try {
      client.awaitReady();
      this.guild = client.getGuildById(ConfigLoader.getGuildId());
      /*Nunu.getInstance().getGuild().updateCommands().addCommands(commands.stream().map(SlashCommand::commandData).toList())
          .addCommands(context.stream().map(contextCommand -> Commands.context(contextCommand.getType(), contextCommand.getName())).toList()).queue();*/
      LeaderboardHandler.handleLeaderboards();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    System.out.println("DONE");
    new Query<>("UPDATE discord_user SET joined = null WHERE joined is not null").update(List.of());
    ScheduleManager.run();
  }

  @Override
  public void disconnect() {
    new Query<>("UPDATE discord_user SET joined = null WHERE joined is not null").update(List.of());
  }

  private void handleEvents() {
    final var adapters = new ArrayList<>(List.of(new BotConfigurator(), new EventLogger(), new InputHandler()));
    adapters.addAll(new EventRegisterer().register());
    client.addEventListener(adapters.toArray());
  }
}
