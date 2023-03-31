package de.zahrie.trues.discord.command.models.leaderboard;

import java.util.Arrays;
import java.util.List;

import de.zahrie.trues.api.discord.builder.leaderboard.Leaderboard;
import de.zahrie.trues.api.discord.builder.queryCustomizer.CustomQuery;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StatsCommandBase {
  public static boolean execute(SlashCommand slashCommand, SlashCommandInteractionEvent event, String prefix) {
    final String queryKey = prefix + slashCommand.find("name").string();
    final CustomQuery.Queries storedQuery = CustomQuery.Queries.valueOf(queryKey);
    final List<String> parameters = Arrays.stream(slashCommand.find("parameters").string().split(",")).toList();
    storedQuery.getCustomQuery().setParameters(parameters);
    final Leaderboard leaderboard = new Leaderboard(storedQuery);
    if (slashCommand.find("public").bool(false)) {
      event.reply("Die Nachricht wird in Kürze erstellt!").setEphemeral(true).queue();
      leaderboard.createNewPublic(slashCommand.getCustomEmbedData(), event);
      return true;
    }
    leaderboard.buildNew(slashCommand.getCustomEmbedData(), event);
    return true;
  }
}
