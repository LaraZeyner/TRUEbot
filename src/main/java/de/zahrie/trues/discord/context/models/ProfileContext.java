package de.zahrie.trues.discord.context.models;

import de.zahrie.trues.api.coverage.player.model.Player;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Column;
import de.zahrie.trues.api.discord.command.slash.annotations.DBQuery;
import de.zahrie.trues.api.discord.command.slash.annotations.Embed;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.riot.matchhistory.game.GameType;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context("Profil ansehen")
public class ProfileContext extends ContextCommand {
  @Override
  @Msg(value = "Profil von {}", embeds = {
      @Embed(description = "Nutzerlevel: **{}**", queries = {
          @DBQuery(query = "riot-account", columns = {@Column("Riot-Account")}),
          @DBQuery(query = "prm-team", columns = {@Column("Prime League Team")}),
          @DBQuery(query = "prm-games", columns = {@Column("Spielzeit"), @Column("Matchup"), @Column("Stats")})
      })
  })
  protected boolean execute(UserContextInteractionEvent event) {
    final String mention = getTargetMember().getAsMention();
    if (getTarget() == null) return reply("Dieser Account existiert nicht.");
    final Player player = getTarget().getPlayer();
    if (player != null) {
      addEmbedData("riot-account", new Object[]{player.toString()});
      if (player.getTeam() != null) handleTeamData(player.getTeam());
      addEmbedData("prm-games", player.getLastGames(GameType.TOURNAMENT));
    }
    return sendMessage(mention);
  }

  private void handleTeamData(Team team) {
    String divisionName = "keine Daten";
    String score = "keine Daten";
    if (team instanceof final PrimeTeam primeTeam) {
      if (primeTeam.getCurrentLeague() != null) {
        divisionName = primeTeam.getCurrentLeague().getName();
        score = primeTeam.getScore().toString();
      }
    }
    addEmbedData("prm-team",
        new Object[]{"Teamname:", team.getFullName()},
        new Object[]{"Aktuelle Division:", divisionName},
        new Object[]{"Aktuelle Platzierung:", score});
  }
}
