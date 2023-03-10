package de.zahrie.trues.api.discord.builder.modal;

import de.zahrie.trues.api.discord.Replyer;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@EqualsAndHashCode(callSuper = true)
public abstract class ModalBase extends Replyer {
  public ModalBase() {
    super(ModalInteractionEvent.class);
    final View annotation = getClass().asSubclass(this.getClass()).getAnnotation(View.class);
    if (annotation == null) {
      return;
    }
    this.name = annotation.value();
  }

  public Modal.Builder create(String title) {
    return Modal.create(name, title);
  }

  protected abstract Modal getModal(boolean value);

  protected abstract void execute(ModalInteractionEvent event);
}
