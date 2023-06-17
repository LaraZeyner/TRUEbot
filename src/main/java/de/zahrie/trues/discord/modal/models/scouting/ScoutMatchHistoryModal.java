package de.zahrie.trues.discord.modal.models.scouting;

import java.util.Arrays;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.community.orgateam.OrgaTeamFactory;
import de.zahrie.trues.api.coverage.participator.model.Lineup;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.performance.Lane;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.discord.scouting.Scouting;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.util.Util;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.Nullable;

@View(value = ModalRegisterer.SCOUT_PLAYER_HISTORY)
@ExtensionMethod(DiscordUserFactory.class)
public class ScoutMatchHistoryModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Scouting: Spieler-Matchhistory")
        .required("1", "Lane/Summonername:", "Lane/Name des Spielers angeben", 16)
        .optional("2", "Champion:", "Championname eintragen ...", 16)
        .required("3", "Scouting-Typ:", Arrays.stream(ScoutingGameType.values()).toList())
        .optional("4", "Lane:", Lane.ITERATE).get();
  }

  @Override
  public boolean execute(@NonNull ModalInteractionEvent event) {
    final Lane lane = getEnum(Lane.class, "1");
    final Player player = determinePlayer(event, lane);
    if (player == null) return reply("Der Spieler wurde nicht gefunden");

    final ScoutingGameType gameType = getEnum(ScoutingGameType.class, "3");
    final Champion champion = getString("2") == null ? null :
        new Query<>(Champion.class).where("champion_name", getString("2")).entity();
    final Lane lane2 = getEnum(Lane.class, "4");
    ScoutingManager.handlePlayerHistory(event, player, champion, Util.avoidNull(gameType, ScoutingGameType.TEAM_GAMES), Util.avoidNull(lane2, lane));
    return true;
  }

  @Nullable
  private Player determinePlayer(@NonNull ModalInteractionEvent event, @Nullable Lane lane) {
    if (lane == null) return getString("4") == null ? null : PlayerFactory.getPlayerFromName(getString("1"));
    else {
      final OrgaTeam team = OrgaTeamFactory.getTeamFromChannel(event.getGuildChannel());
      final Scouting scouting = ScoutingManager.forTeam(team);
      return scouting == null ? null : scouting.participator().getTeamLineup().getLineup().stream()
          .filter(lineup -> lineup.getLane().equals(lane)).map(Lineup::getPlayer).findFirst().orElse(null);
    }
  }
}
