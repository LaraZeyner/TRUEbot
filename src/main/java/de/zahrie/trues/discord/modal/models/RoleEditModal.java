package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.util.Util;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.ROLE_EDIT)
public class RoleEditModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    final var granter = new RoleGranter(getInvoker(), getTarget());
    final boolean add = !granter.getAssignGroups().isEmpty();
    final boolean remove = !granter.getRemoveGroups().isEmpty();
    return create("Rollen von " + getTargetMember().getNickname() + " bearbeiten")
        .addComponents(getTargetUser(), getAddRemove(add, remove), getGroups(), getDaysField()).build();
  }

  @Override
  @Msg("Die Rollen wurden bearbeitet.")
  public boolean execute(ModalInteractionEvent event) {
    final DiscordUser invoker = DiscordUserFactory.getDiscordUser(Util.nonNull(event.getMember()));
    new RoleGranter(invoker, getInvoker()).add(getGroup(), getDays());
    return sendMessage();
  }
}
