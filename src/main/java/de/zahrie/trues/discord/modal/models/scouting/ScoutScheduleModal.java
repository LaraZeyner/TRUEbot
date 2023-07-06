package de.zahrie.trues.discord.modal.models.scouting;

import de.zahrie.trues.api.coverage.team.model.AbstractTeam;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.discord.scouting.ScoutingType;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.SCOUT_SCHEDULE)
@ExtensionMethod(DiscordUserFactory.class)
public class ScoutScheduleModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Scouting: Team-Schedule")
        .optional("1", "gegnerisches Team:", "TeamID oder voller Name (sofern bekannt)", 100).get();
  }

  @Override
  public boolean execute(ModalInteractionEvent event) {
    final AbstractTeam team2 = determineTeam();
    if (team2 == null) return reply("Der Gegner konnte nicht gefunden werden.");

    ScoutingManager.custom(team2, event, ScoutingType.SCHEDULE);
    return true;
  }
}
