package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.ROLE_EDIT)
@ExtensionMethod(StringUtils.class)
public class RoleEditModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Rollen von " + Util.avoidNull(target.getNickname(), "null").keep(23) + " bearbeiten")
        .single("1", "Typ", "hinzufügen, entfernen", 10)
        .single("2", "Rollenname", "Rollenname hier eintragen", 50)
        .single("3", "Dauer in Tagen", "0 = unendlich", 3).get();
  }

  @Override
  @Msg("Die Rollen wurden bearbeitet.")
  public boolean execute(ModalInteractionEvent event) {
    final DiscordUser invoker = DiscordUserFactory.getDiscordUser(Util.nonNull(event.getMember()));
    final RoleGranter granter = new RoleGranter(invoker, target);
    final Integer days = getInt("3");
    if (getString("1").equalsIgnoreCase("hinzufügen")) {
      if (days <= 0) {
        granter.getAssignGroups().stream().filter(discordGroup -> discordGroup.getName().equalsIgnoreCase(getString("2"))).findFirst().ifPresent(granter::add);
      } else {
        granter.getAssignGroups().stream().filter(discordGroup -> discordGroup.getName().equalsIgnoreCase(getString("2"))).findFirst().ifPresent(discordGroup -> granter.add(discordGroup, days));
      }
    } else {
      granter.getRemoveGroups().stream().filter(discordGroup -> discordGroup.getName().equalsIgnoreCase(getString("2"))).findFirst().ifPresent(granter::remove);
    }

    return sendMessage();
  }
}
