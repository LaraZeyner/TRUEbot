package de.zahrie.trues.discord.modal.models;

import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.match.model.Scrimmage;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.SCRIM_CREATE)
public class ScrimCreateModal extends ModalImpl {
  @Override
  protected Modal getModal(boolean value) {
    return create("Scrim erstellen")
        .addComponents(getTeams(), getTeamIdField(10), getDateField(), getOpggField(true), getOpggField(false)).build();
  }

  @Override
  @Msg(value = "Das Scrim wurde hinzugefügt.", error = "Das Lineup ist nicht vollständig oder fehlerhaft.")
  protected boolean execute(ModalInteractionEvent event) {
    final LocalDateTime time = getTime();
    if (time == null) return reply("Das Datum ist fehlerhaft.");

    final Scrimmage scrimmage = new Scrimmage(time);
    if (getTeam() == null) return reply("Dein Team konnte nicht gefunden werden.");

    final var home = new Participator(true, getTeam().getTeam());
    if (handleTeamsLineup(home)) return errorMessage();

    if (getTeamIdOrName() == null) return reply("Der Gegner konnte nicht gefunden werden.");

    final var guest = new Participator(false, getTeamIdOrName());
    if (handleTeamsLineup(guest)) return errorMessage();

    Database.insert(scrimmage);
    scrimmage.addParticipators(home, guest);
    return sendMessage();
  }
}
