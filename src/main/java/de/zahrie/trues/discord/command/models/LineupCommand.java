package de.zahrie.trues.discord.command.models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.model.Lineup;
import de.zahrie.trues.api.coverage.participator.model.Participator;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.api.riot.performance.Lane;
import de.zahrie.trues.discord.scouting.Scouting;
import de.zahrie.trues.discord.scouting.ScoutingManager;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.util.Util;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "lineup", descripion = "Lineup des Gegners angeben", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "opgg", description = "Op.gg-Link", required = false),
    @Option(name = "top", description = "Op.gg-Link", required = false),
    @Option(name = "jungle", description = "ID oder Summonername", required = false),
    @Option(name = "middle", description = "ID oder Summonername", required = false),
    @Option(name = "bottom", description = "ID oder Summonername", required = false),
    @Option(name = "support", description = "ID oder Summonername", required = false),
    @Option(name = "matchid", description = "ID des Matches", required = false, type = OptionType.INTEGER)
})
@ExtensionMethod(StringUtils.class)
public class LineupCommand extends SlashCommand {
  @Override
  @Msg(value = "Das Lineup wurde eingetragen.", error = "Der Channel ist keinem Team zugewiesen.")
  public boolean execute(SlashCommandInteractionEvent event) {
    final OrgaTeam locatedTeam = getLocatedTeam();
    if (locatedTeam == null) return errorMessage();

    final Team team = locatedTeam.getTeam();
    if (team == null) return reply("Dieses Orgateam hat kein Team.");

    final Integer matchId = find("matchid").integer();
    final Match mostRecentAMatch = (matchId == null) ? team.getMatches().getNextMatch(true) : new Query<>(Match.class).entity(matchId);
    if (mostRecentAMatch == null) return reply("Es wurde kein Match gefunden.");

    for (Participator participator : mostRecentAMatch.getParticipators()) {
      if (participator.getTeam().equals(team)) continue;
      final boolean orderedLineup = determineOrderedLineup(participator);
      if (!orderedLineup) {
        return reply("Das Lineup ist nicht vollständig.");
      }
      final Scouting scouting = ScoutingManager.forTeam(locatedTeam);
      if (scouting != null) scouting.update();
      return sendMessage();
    }

    // TODO (Abgie) 27.03.2023: update Scout-pages
    return reply("Es wurde kein Team gefunden.");
  }

  private boolean determineOrderedLineup(Participator participator) {
    final String opGg = find("opgg").string();
    final List<Player> players = new ArrayList<>(List.of(determinePlayerOfKey("top", participator),
        determinePlayerOfKey("jungle", participator), determinePlayerOfKey("middle", participator),
        determinePlayerOfKey("bottom", participator), determinePlayerOfKey("support", participator)));
    if (opGg == null) return false;
    return participator.getTeamLineup().setOrderedLineup(opGg, players);
  }

  public Player determinePlayerOfKey(String key, Participator participator) {
    final String data = find(key).string();
    if (data != null) {
      if (Pattern.compile("-?\\d+(\\.\\d+)?").matcher(data).matches()) {
        final int playerId = Integer.parseInt(data);
        final var player = new Query<>(Player.class).entity(playerId);
        if (player != null) return player;
      }

      final Player player = PlayerFactory.getPlayerFromName(data);
      if (player != null) return player;
    }

    final Lane lane = key.toEnum(Lane.class);
    final Lineup lineup = participator.getTeamLineup().getStoredLineup(lane);
    return Util.avoidNull(lineup, null, Lineup::getPlayer);
  }
}
