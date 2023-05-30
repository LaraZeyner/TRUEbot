package de.zahrie.trues.discord.command.models;


import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.discord.builder.leaderboard.Leaderboard;
import de.zahrie.trues.api.discord.builder.queryCustomizer.NamedQuery;
import de.zahrie.trues.api.discord.builder.queryCustomizer.SimpleCustomQuery;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "stats", descripion = "Ein Leaderboard erzeugen", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "name", description = "Name des Leaderboards", choices = {"orga_games", "team_champions"}),
    @Option(name = "parameters", description = "Parameter (durch Komma getrennt)", required = false),
    @Option(name = "public", description = "öffentliches Leaderboard", required = false, type = OptionType.BOOLEAN)
})
public class StatsCommand extends SlashCommand {
  @Override
  public boolean execute(SlashCommandInteractionEvent event) {
    return execute(this, event);
  }

  public static boolean execute(SlashCommand slashCommand, SlashCommandInteractionEvent event) {
    final String queryKey = slashCommand.find("name").string().toUpperCase();
    final NamedQuery namedQuery = NamedQuery.valueOf(queryKey);
    final List<String> parameters = Arrays.stream(slashCommand.find("parameters").string().split(",")).toList();
    final SimpleCustomQuery customQuery = SimpleCustomQuery.params(namedQuery, parameters.stream().map(string -> (Object) string).toList());
    final Leaderboard leaderboard = new Leaderboard(customQuery);
    if (slashCommand.find("public").bool(false)) {
      event.reply("Die Nachricht wird in Kürze erstellt!").setEphemeral(true).queue();
      leaderboard.createNewPublic(slashCommand.getCustomEmbedData(), event);
      return true;
    }
    leaderboard.buildNew(slashCommand.getCustomEmbedData(), event);
    return true;
  }
}
