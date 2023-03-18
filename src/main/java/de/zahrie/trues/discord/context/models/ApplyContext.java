package de.zahrie.trues.discord.context.models;

import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Bewerbung erstellen")
public class ApplyContext extends ContextCommand {
  @Override
  @UseView(ModalRegisterer.APPLY)
  protected boolean execute(UserContextInteractionEvent event) {
    final boolean isAdvanced = getInvoker().isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN);
    return sendModal(isAdvanced);
  }
}
