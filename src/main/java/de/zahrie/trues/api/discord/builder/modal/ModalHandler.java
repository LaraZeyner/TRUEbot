package de.zahrie.trues.api.discord.builder.modal;

import java.util.List;

import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
@Data
public class ModalHandler extends ListenerAdapter {
  protected static final List<ModalImpl> modals = new ModalRegisterer().register();

  @Override
  public void onModalInteraction(@NotNull ModalInteractionEvent event) {
    for (ModalImpl modal : modals) {
      if (modal.getName().equals(event.getModalId())) {
        modal.setEvent(event);
        modal.execute(event);
        break;
      }
    }
  }

  public record Find(DiscordUser invoker, DiscordUser target) {
    public Modal getModal(String type, boolean someBool) {
      final ModalImpl base = modals.stream().filter(modalBase -> modalBase.getName().equals(type)).findFirst().orElse(null);
      if (base == null) {
        return null;
      }
      base.setTarget(target);
      return base.getModal(someBool);
    }
  }
}
