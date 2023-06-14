package de.zahrie.trues.discord.modal.models;

import java.util.Arrays;

import de.zahrie.trues.api.community.application.Application;
import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.discord.util.Nunu;
import de.zahrie.trues.api.logging.ServerLog;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.APPLY_STAFF)
@ExtensionMethod(DiscordUserFactory.class)
public class ApplyStaffModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Staff-Bewerbung erstellen")
        .requiredMulti("1", "Position in der Orga", Arrays.stream(TeamPosition.values())
            .filter(position -> position.ordinal() >= TeamPosition.TEAM_COACH.ordinal()).toList())
        .requiredMulti("2", "Beschreibung", "Erzähle uns etwas über dich...", 1000).get();
  }

  @Override
  @Msg("Die Bewerbung wurde abgeschickt.")
  public boolean execute(ModalInteractionEvent event) {
    final TeamPosition position = getEnum(TeamPosition.class, "1");
    final Application application = target.apply(TeamRole.ORGA, position, getString("2"));
    new ServerLog(event.getMember() == null ? null :
        DiscordUserFactory.getDiscordUser(event.getMember()), target, application.toString(), ServerLog.ServerLogAction.APPLICATION_CREATED).create();
    Nunu.DiscordChannel.getAdminChannel().sendMessage("Neuer Bewerber " + target.getNickname() + ": " + application).queue();
    return sendMessage();
  }
}
