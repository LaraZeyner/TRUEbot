package de.zahrie.trues.discord.command.models;

import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.discord.builder.leaderboard.Leaderboard;
import de.zahrie.trues.api.discord.builder.leaderboard.LeaderboardHandler;
import de.zahrie.trues.api.discord.builder.queryCustomizer.NamedQuery;
import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "stats", descripion = "Ein Leaderboard erzeugen", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "name", description = "Name des Leaderboards",
        choices = {"champions", "orga_elos", "orga_prm", "orga_schedule", "season_champions"}),
    @Option(name = "parameters", description = "Parameter (durch Komma getrennt)", required = false),
    @Option(name = "public", description = "öffentliches Leaderboard", required = false, type = OptionType.BOOLEAN)
})
public class StatsCommand extends SlashCommand {
  @Override
  @Msg(value = "Die Nachricht wird in Kürze erstellt!", error = "Du hast nicht genügend TRUEs. Verfügbar: {} TRUEs")
  public boolean execute(SlashCommandInteractionEvent event) {
    final String queryKey = find("name").string().toUpperCase();
    final NamedQuery namedQuery = NamedQuery.valueOf(queryKey);
    final String paramsString = find("parameters").string();
    final List<String> parameters = Arrays.stream(Util.avoidNull(paramsString, new String[0], str -> str.split(","))).toList();
    final SimpleCustomQuery customQuery = SimpleCustomQuery.params(namedQuery, parameters.stream().map(string -> (Object) string).toList());
    final Leaderboard leaderboard = new Leaderboard(customQuery);
    if (find("public").bool(false)) {
      sendMessage();
      try {
        final var publicLeaderboard = leaderboard.createNewPublic(event);
        Thread.sleep(1000L);
        LeaderboardHandler.add(publicLeaderboard);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return true;
    }
    leaderboard.buildNew(event);
    return true;
  }
}
