package de.zahrie.trues.discord.context.models;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.AbstractTeam;
import de.zahrie.trues.api.discord.builder.queryCustomizer.NamedQuery;
import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Profil ansehen")
public class ProfileContext extends ContextCommand {
  @Override
  @Msg(value = "Profil von {}", description = "Nutzerlevel: **{}**")
  protected boolean execute(UserContextInteractionEvent event) {
    final String mention = getTarget().getNickname();
    if (getTarget() == null) return reply("Dieser Account existiert nicht.");

    final Player player = getTarget().getPlayer();
    if (player != null) {
      addEmbedData(NamedQuery.PROFILE_RIOT_ACCOUNT, new Object[]{player.toString()});
      if (player.getTeam() != null) handleTeamData(player.getTeam());
      addEmbed(NamedQuery.PROFILE_PRM_GAMES, player.getPuuid());
    }

    if (getInvoker().isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN)) addEmbed(NamedQuery.STRIKES, getTarget().getId());
    return sendMessage(mention);
  }

  private void handleTeamData(AbstractTeam team) {
    String divisionName = "keine Daten";
    String score = "keine Daten";
    if (team instanceof PRMTeam prmTeam) {
      final LeagueTeam currentLeague = prmTeam.getCurrentLeague();
      if (currentLeague != null) {
        divisionName = currentLeague.getLeague().getName();
        score = currentLeague.getScore().toString();
      }
    }
    addEmbedData(NamedQuery.PROFILE_PRM_TEAM,
        new Object[]{"Teamname:", team.getFullName()},
        new Object[]{"Aktuelle Division:", divisionName},
        new Object[]{"Aktuelle Platzierung:", score});
  }
}
