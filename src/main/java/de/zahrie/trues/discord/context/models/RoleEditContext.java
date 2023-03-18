package de.zahrie.trues.discord.context.models;

import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Rollen bearbeiten")
public class RoleEditContext extends ContextCommand {
  @Override
  @Msg(value = "Der Nutzer wurde bearbeitet.")
  @UseView(ModalRegisterer.ROLE_EDIT)
  public boolean execute(UserContextInteractionEvent event) {
    setPermission(o -> new RoleGranter(o, getTarget()).isNotEmpty());
    return sendModal();
  }
}
