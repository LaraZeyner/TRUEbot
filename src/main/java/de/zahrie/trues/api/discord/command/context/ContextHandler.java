package de.zahrie.trues.api.discord.command.context;

import java.util.List;

import de.zahrie.trues.discord.context.ContextRegisterer;
import de.zahrie.trues.api.discord.util.Nunu;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class ContextHandler extends ListenerAdapter {
  private static final List<ContextCommand> commands = new ContextRegisterer().register();

  public static void handleCommands() {
    Nunu.getInstance().getClient().updateCommands().addCommands(
        commands.stream().map(contextCommand -> Commands.context(contextCommand.getType(), contextCommand.getName())).toList()
    ).queue();
  }

  @Override
  public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
    for (ContextCommand contextCommand : commands) {
      if (contextCommand.getName().equals(event.getName())) {
        contextCommand.handleCommand(event);
        return;
      }
    }
    event.reply("Dieser Command wurde nicht gefunden!").queue();
  }

  @Override
  public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
    super.onMessageContextInteraction(event);
  }
}
