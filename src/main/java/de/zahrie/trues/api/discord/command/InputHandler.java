package de.zahrie.trues.api.discord.command;

import java.util.List;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.util.Willump;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

public class InputHandler extends ListenerAdapter {
  private static final List<ModalImpl> modals = new ModalRegisterer().register();

  @Override
  public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    for (SlashCommand slashCommand : Willump.getCommands()) {
      if (slashCommand.getName().equals(event.getName())) {
        slashCommand.handleCommand(event);
        return;
      }
    }
    event.reply("Dieser Command wurde nicht gefunden!").queue();
  }

  @Override
  public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
    Willump.getCommands().stream().filter(slashCommand -> slashCommand.getName().equals(event.getName())).findFirst()
        .ifPresent(slashCommand -> slashCommand.handleAutoCompletion(event));
  }

  public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
    for (ContextCommand contextCommand : Willump.getContext()) {
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

  @Override
  public void onModalInteraction(@NotNull ModalInteractionEvent event) {
    for (ModalImpl modal : modals) {
      if (modal.getName().equals(event.getModalId())) {
        modal.setEvent(event);
        event.deferReply(true).queue();
        modal.execute(event);
        break;
      }
    }
  }

  public record Find(DiscordUser invoker, DiscordUser target, IReplyCallback event) {
    public Modal getModal(String type, boolean someBool) {
      final ModalImpl base = modals.stream().filter(modalBase -> modalBase.getName().equals(type)).findFirst().orElse(null);
      if (base == null) return null;

      base.setTarget(target);
      return base.getModal(someBool);
    }
  }
}
