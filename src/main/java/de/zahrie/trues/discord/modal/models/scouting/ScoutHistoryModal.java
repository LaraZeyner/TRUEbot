package de.zahrie.trues.discord.modal.models.scouting;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.discord.scouting.Scouting;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.SCOUT_HISTORY)
@ExtensionMethod(DiscordUserFactory.class)
public class ScoutHistoryModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Scouting: Team-Matchhistory")
        .addComponents(getTeamIdField(100), getScoutingGameTypeField(), getDurationField(), getPageField()).build();
  }

  @Override
  public boolean execute(ModalInteractionEvent event) {
    ScoutingManager.custom(getTeamIdOrName(), event, Scouting.ScoutingType.HISTORY, getScoutingGameType(), getDuration(), getPage());
    return true;
  }
}
