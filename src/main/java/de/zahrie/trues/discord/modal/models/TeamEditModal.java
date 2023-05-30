package de.zahrie.trues.discord.modal.models;

import java.util.Arrays;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.TEAM_EDIT)
@ExtensionMethod(StringUtils.class)
public class TeamEditModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create(Util.avoidNull(target.getNickname(), "null").keep(25) + " für Team bearbeiten")
        .single("1", "Team auswählen", new Query<>(OrgaTeam.class).get("team_abbr_created", String.class), 10)
        .single("2", "Rolle im Team", Arrays.stream(TeamRole.values()).filter(role -> role.getName() != null).toList())
        .single("3", "Position im Team", Arrays.stream(TeamPosition.values())
            .filter(position -> position.ordinal() <= TeamPosition.TEAM_COACH.ordinal()).toList())
        .single("4", "Als Capitän?", "ja, nein", 4).get();
  }

  @Override
  @Msg("Der Nutzer wurde bearbeitet.")
  public boolean execute(ModalInteractionEvent event) {
    final OrgaTeam team = new Query<>(OrgaTeam.class).where("team_abbr_created", getString("1")).entity();
    if (team == null) return reply("Das Team wurde nicht gefunden.");

    final TeamRole role = getEnum(TeamRole.class, "2");
    if (role == null) return reply("Fehlerhafte Rolle!");

    final TeamPosition position = getEnum(TeamPosition.class, "3");
    if (position == null) return reply("Fehlerhafte Position!");

    final Boolean asCaptain = getBoolean("4");

    if (role.equals(TeamRole.REMOVE)) {
      team.getRoleManager().removeRole(target);
      return reply("Der Nutzer wurde aus dem Team entfernt.");
    }

    team.getRoleManager().addRole(target, role, position);
    if (asCaptain) team.getRoleManager().addCaptain(target);
    else team.getRoleManager().removeCaptain(target);
    return sendMessage();
  }
}
