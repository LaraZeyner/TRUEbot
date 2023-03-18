package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.member.DiscordMember;
import de.zahrie.trues.api.discord.member.DiscordMemberFactory;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.models.community.application.Application;
import de.zahrie.trues.models.community.application.TeamRole;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.APPLY_STAFF)
public class ApplyStaffModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    final ActionRow memberSelection = value ? getMembers() : getTargetUser();
    return create("Staff-Bewerbung erstellen")
        .addComponents(memberSelection, getStaffPosition(), getTextField(1000)).build();
  }

  @Override
  @Msg("Die Bewerbung wurde abgeschickt.")
  public boolean execute(ModalInteractionEvent event) {
    final DiscordMember target = getTarget() == null ? DiscordMemberFactory.getMember(getMember()) : getTarget();
    final Application application = new Application(target, TeamRole.ORGA, getTeamPosition(), getDescription());
    Database.save(application);
    //TODO (Abgie) 10.03.2023: Admin-Message
    return sendMessage();
  }
}
