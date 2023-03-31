package de.zahrie.trues.discord.command.models.leaderboard;


import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "leaderboard", descripion = "Ein Leaderboard erzeugen", perm = @Perm(PermissionRole.EVENT))
public class StatsCommand extends SlashCommand {
  public StatsCommand() {
    super(new GlobalStatsCommand(), new TeamStatsCommand());
  }

  @Override
  public boolean execute(SlashCommandInteractionEvent event) {
    return false;
  }
}
