package de.zahrie.trues.discord.context.models;

import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Mitglied hinzufügen")
public class MembersEditContext extends ContextCommand {
  @Override
  @Msg(value = "Der Nutzer wurde bearbeitet.")
  @UseView(ModalRegisterer.MEMBER_EDIT)
  public boolean execute(UserContextInteractionEvent event) {
    setPermission(o -> o.getActiveGroups().stream().anyMatch(p -> p.getTier().isAdmin()));
    return sendModal();
  }
}
