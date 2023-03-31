package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.MEMBER_EDIT)
@ExtensionMethod(DiscordUserFactory.class)
public class MembersEditModal extends ModalImpl {
  @Override
  protected Modal getModal(boolean value) {
    Modal.Builder builder = create(getTargetMember().getNickname() + " als Mitglied hinzufügen (nicht Spieler)");
    return builder.addComponents(getTargetUser(), getApplicationRoleField2(), getMemberGroups()).build();
  }

  @Override
  @Msg("Der Nutzer wurde bearbeitet.")
  protected boolean execute(ModalInteractionEvent event) {
    getTarget().addOrgaRole(getTeamRole(), getMemberGroup());
    return sendMessage();
  }
}
