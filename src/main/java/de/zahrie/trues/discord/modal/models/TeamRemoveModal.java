package de.zahrie.trues.discord.modal.models;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

//@View(ModalRegisterer.TEAM_REMOVE)
@Deprecated(forRemoval = true)
public class TeamRemoveModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create(target.getNickname() + " aus Team entfernen")
        .required("1", "Team auswählen", new Query<>(OrgaTeam.class).get("team_abbr_created", String.class), 10)
        .get();
  }

  @Override
  @Msg("Der Nutzer wurde aus dem Team entfernt.")
  public boolean execute(ModalInteractionEvent event) {
    final String teamAbbr = getString("1");
    final OrgaTeam team = new Query<>(OrgaTeam.class).where("team_abbr_created", teamAbbr).entity();
    team.getRoleManager().removeRole(getTarget());
    return sendMessage();
  }
}
