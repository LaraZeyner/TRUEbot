package de.zahrie.trues.discord.modal.models;

import java.util.Arrays;
import java.util.List;

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

@View(value = ModalRegisterer.APPLY)
@ExtensionMethod(DiscordUserFactory.class)
public class ApplyModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Team-Bewerbung erstellen")
        .single("1", "Rolle im Team", List.of(TeamRole.MAIN, TeamRole.SUBSTITUTE))
        .multi("2", "Position im Team", Arrays.stream(TeamPosition.values())
            .filter(position -> position.ordinal() <= TeamPosition.TEAM_COACH.ordinal()).toList())
        .multi("3", "Beschreibung", "Erzähle uns etwas über dich...", 1000).get();
  }

  @Override
  @Msg("Die Bewerbung wurde abgeschickt.")
  public boolean execute(ModalInteractionEvent event) {
    final TeamRole role = getEnum(TeamRole.class, "1");
    final TeamPosition position = getEnum(TeamPosition.class, "2");
    final Application application = target.apply(role, position, getString("3"));
    new ServerLog(event.getMember() == null ? null : DiscordUserFactory.getDiscordUser(event.getMember()), target, application.toString(), ServerLog.ServerLogAction.APPLICATION_CREATED).create();
    Nunu.DiscordChannel.getAdminChannel().sendMessage("Neuer Bewerber " + target.getNickname() + ": " + application).queue();
    return sendMessage();
  }
}
