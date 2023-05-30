package de.zahrie.trues.discord.modal.models.scouting;

import java.util.Arrays;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.builder.modal.ModalImpl;
import de.zahrie.trues.api.discord.builder.modal.View;
import de.zahrie.trues.api.discord.user.DiscordUserFactory;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.discord.scouting.Scouting;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

@View(value = ModalRegisterer.SCOUT_MATCHUPS)
@ExtensionMethod(DiscordUserFactory.class)
public class ScoutMatchupsModal extends ModalImpl {
  @Override
  public Modal getModal(boolean value) {
    return create("Scouting: Team-Matchups")
        .single("1", "gegnerisches Team:", "TeamID oder voller Name (sofern bekannt)", 100)
        .single("2", "Scouting-Typ:", Arrays.stream(ScoutingGameType.values()).toList())
        .single("3", "Tage:", "Scoutingdauer in Tagen", 3).get();
  }

  @Override
  public boolean execute(ModalInteractionEvent event) {
    Team team2 = null;
    final Integer anInt = getInt("1");
    if (anInt != null) team2 = new Query<>(Team.class).where("prm_id", anInt).entity();
    if (team2 == null) team2 = new Query<>(Team.class).where("team_name", anInt).entity();
    if (team2 == null) return reply("Der Gegner konnte nicht gefunden werden.");
    final ScoutingGameType gameType = getEnum(ScoutingGameType.class, "2");
    final Integer duration = getInt("3");

    ScoutingManager.custom(team2, event, Scouting.ScoutingType.PLAYER_HISTORY, gameType, duration == null ? 180 : duration);
    return true;
  }
}
