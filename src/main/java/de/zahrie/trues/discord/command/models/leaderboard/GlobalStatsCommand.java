package de.zahrie.trues.discord.command.models.leaderboard;


import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "globalstats", descripion = "Ein Leaderboard erzeugen", perm = @Perm(PermissionRole.ORGA_MEMBER), options = {
    @Option(name = "name", description = "Name des Leaderboards", choices = {"Champions"}),
    @Option(name = "parameters", description = "Parameter (durch Komma getrennt)", required = false),
    @Option(name = "public", description = "öffentliches Leaderboard", required = false, type = OptionType.BOOLEAN)
})
public class GlobalStatsCommand extends SlashCommand {
  @Override
  public boolean execute(SlashCommandInteractionEvent event) {
    return StatsCommandBase.execute(this, event, "TEAM_");
  }
}
