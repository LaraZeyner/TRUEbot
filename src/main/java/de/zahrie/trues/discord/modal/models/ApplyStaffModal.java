package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.logging.ServerLog;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.APPLY_STAFF)
@ExtensionMethod(DiscordUserFactory.class)
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
    final DiscordUser target = getTarget() == null ? DiscordUserFactory.getDiscordUser(getMember()) : getTarget();
    final Application application = target.apply(TeamRole.ORGA, getTeamPosition(), getDescription());
    new ServerLog(getInvoker(), target, application.toString(), ServerLog.ServerLogAction.APPLICATION_CREATED).create();
    Nunu.DiscordChannel.getAdminChannel().sendMessage("Neuer Bewerber " + getInvoker().getMention() + ": " + application).queue();
    return sendMessage();
  }
}
