package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.TEAM_EDIT)
public class TeamEditModal extends ModalImpl {
  @Override
  protected Modal getModal(boolean value) {
    Modal.Builder builder = create(getTarget().getMember().getNickname() + " zu Team hinzufügen/bearbeiten")
        .addComponents(getTargetUser());
    if (value) {
      builder = builder.addComponents(getTeams());
    }
    return builder.addComponents(getApplicationRoleField(), getApplicationPosition(), getBool()).build();
  }

  @Override
  @Msg("Der Nutzer wurde bearbeitet.")
  protected boolean execute(ModalInteractionEvent event) {
    getTeam().addRole(getInvoker(), getTeamRole(), getTeamPosition());
    if (getBoolValue()) {
      getTeam().addCaptain(getInvoker());
    } else {
      getTeam().removeCaptain(getInvoker());
    }
    return sendMessage();
  }
}
