package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

//@View(ModalRegisterer.TEAM_REMOVE)
@Deprecated(forRemoval = true)
public class TeamRemoveModal extends ModalImpl {
  @Override
  protected Modal getModal(boolean value) {
    return create(getTargetMember().getNickname() + " aus Team entfernen")
        .addComponents(getTargetUser(), getTeams()).build();
  }

  @Override
  @Msg("Der Nutzer wurde aus dem Team entfernt.")
  protected boolean execute(ModalInteractionEvent event) {
    getTeam().getRoleManager().removeRole(getInvoker());
    return sendMessage();
  }
}
