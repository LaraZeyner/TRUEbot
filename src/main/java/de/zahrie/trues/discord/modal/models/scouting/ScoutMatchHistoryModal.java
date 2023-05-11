package de.zahrie.trues.discord.modal.models.scouting;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.coverage.participator.model.Lineup;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.riot.performance.Lane;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.discord.scouting.Scouting;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.util.Util;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.SCOUT_PLAYER_HISTORY)
@ExtensionMethod(DiscordUserFactory.class)
public class ScoutMatchHistoryModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Scouting: Spieler-Matchhistory")
        .addComponents(getLolPositionOrNameField(), getChampionField(), getScoutingGameTypeField(), getLolPositionField()).build();
  }

  @Override
  public boolean execute(ModalInteractionEvent event) {
    final Object positionOrName = getLolPositionOrSummonername();
    Player player = null;
    Lane lane = null;
    if (positionOrName instanceof Lane selectedLane) {
      final OrgaTeam team = OrgaTeamFactory.getTeamFromChannel(event.getGuildChannel());
      final Scouting scouting = ScoutingManager.forTeam(team);
      if (scouting != null) {
        player = scouting.participator().getTeamLineup().getFixedLineups().stream()
            .filter(lineup -> lineup.getLane().equals(selectedLane))
            .map(Lineup::getPlayer).findFirst().orElse(null);
        lane = selectedLane;
      }
    } else if (positionOrName instanceof Player pl) {
      player = pl;
    }
    if (player != null) {
      ScoutingManager.handlePlayerHistory(event, player, getChampion(), Util.avoidNull(getScoutingGameType(), ScoutingGameType.TEAM_GAMES), lane == null ? getLolPosition() : lane);
      return true;
    }
    reply("Der Spieler wurde nicht gefunden");
    return false;
  }
}
