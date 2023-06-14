package de.zahrie.trues.discord.modal.models;

import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.MEMBER_EDIT)
@ExtensionMethod({DiscordUserFactory.class, StringUtils.class})
public class MembersEditModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create(Util.avoidNull(target.getNickname(), "null").keep(18) + " hinzufügen (nicht Spieler)")
        .required("1", "Rolle in der Orga", List.of(TeamRole.TRYOUT, TeamRole.MAIN))
        .requiredMulti("2", "Nicht Spieler", Arrays.stream(TeamPosition.values())
            .filter(position -> position.ordinal() >= TeamPosition.COACH.ordinal()).toList()).get();
  }

  @Override
  @Msg("Der Nutzer wurde bearbeitet.")
  public boolean execute(ModalInteractionEvent event) {
    final TeamRole role = getEnum(TeamRole.class, "1");
    if (role == null) return reply("Fehlerhafte Rolle!");

    final TeamPosition position = getEnum(TeamPosition.class, "2");
    if (position == null) return reply("Fehlerhafte Position!");

    target.addOrgaRole(role, position);
    return sendMessage();
  }
}
