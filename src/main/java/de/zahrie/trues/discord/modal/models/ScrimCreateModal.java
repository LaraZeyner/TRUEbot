package de.zahrie.trues.discord.modal.models;

import java.time.LocalDateTime;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Scrimmage;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.AbstractTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.util.StringUtils;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.SCRIM_CREATE)
public class ScrimCreateModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Scrim erstellen")
        .required("1", "Team auswählen:", new Query<>(OrgaTeam.class).get("team_abbr_created", String.class), 10)
        .required("2", "gegnerisches Team:", "TeamID oder voller Name (sofern bekannt)", 100)
        .required("3", "Matchzeit:", "Spielstartzeitpunkt", 30)
        .requiredMulti("4", "euer op.gg:", "Multi-Op.gg eures Teams", 1000)
        .requiredMulti("5", "gegn. op.gg:", "Multi-Op.gg eures Gegners", 1000).get();
  }

  @Override
  @Msg(value = "Das Scrim wurde hinzugefügt.", error = "Das Lineup ist nicht vollständig oder fehlerhaft.")
  public boolean execute(ModalInteractionEvent event) {
    final OrgaTeam orgaTeam = new Query<>(OrgaTeam.class).where("team_abbr_created", getString("1")).entity();
    if (orgaTeam == null) return reply("Dein Team konnte nicht gefunden werden.");

    final AbstractTeam team = orgaTeam.getTeam();
    if (team == null) return reply("Dein Team konnte nicht gefunden werden.");

    AbstractTeam team2 = null;
    final Integer teamId = getInt("2");
    if (teamId != null) {
      final TeamLoader teamLoader = TeamFactory.getTeamLoader(teamId);
      if (teamLoader == null) return reply("Der Gegner konnte nicht gefunden werden.");
      team2 = teamLoader.load().getTeam();
    }
    if (team2 == null) team2 = new Query<>(AbstractTeam.class).where("team_name", getString("2")).entity();
    if (team2 == null) team2 = new Team(getString("2"), getString("2")).create();

    final LocalDateTime time = StringUtils.getDateTime(getString("3"));
    if (time == null) return reply("Das Datum ist fehlerhaft.");

    team2.setRefresh(time);
    final Scrimmage scrimmage = new Scrimmage(time).create();

    final Participator home = scrimmage.addParticipator(team, true, team2);
    if (handleTeamsLineup(home, true)) return errorMessage();

    final Participator guest = scrimmage.addParticipator(team2, false, team);
    if (handleTeamsLineup(guest, false)) return errorMessage();

    return reply("Das Scrim wurde hinzugefügt.");
  }

  private boolean handleTeamsLineup(Participator participator, boolean isHome) {
    return !participator.getTeamLineup().setOrderedLineup(getString(isHome ? "4" : "5"));
  }
}
