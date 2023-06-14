package de.zahrie.trues.discord.modal.models;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.DiscordGroup;
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
        .required("1", "Typ", "hinzufügen, entfernen", 10)
        .required("2", "Rollenname", List.of(DiscordGroup.FRIEND, DiscordGroup.SCRIMPARTNER))
        .required("3", "Dauer in Tagen", "0 = unendlich", 3).get();
  }

  @Override
  @Msg(value = "Die Rollen wurden bearbeitet.", error = "Du hast keine ausreichenden Rechte.")
  public boolean execute(ModalInteractionEvent event) {
    final AtomicBoolean success = new AtomicBoolean(true);
    final DiscordUser invoker = DiscordUserFactory.getDiscordUser(Util.nonNull(event.getMember()));
    final RoleGranter granter = new RoleGranter(invoker, target);
    final Integer days = getInt("3");
    if (getString("1").equalsIgnoreCase("hinzufügen")) {
      if (days <= 0) {
        granter.getAssignGroups().stream().filter(discordGroup -> discordGroup.getName().equalsIgnoreCase(getString("2"))).findFirst().ifPresentOrElse(granter::add, () -> success.set(false));
      } else {
        granter.getAssignGroups().stream().filter(discordGroup -> discordGroup.getName().equalsIgnoreCase(getString("2"))).findFirst().ifPresentOrElse(discordGroup -> granter.add(discordGroup, days), () -> success.set(false));
      }
    } else {
      granter.getRemoveGroups().stream().filter(discordGroup -> discordGroup.getName().equalsIgnoreCase(getString("2"))).findFirst().ifPresentOrElse(granter::remove, () -> success.set(false));
    }

    return send(success.get());
  }
}
