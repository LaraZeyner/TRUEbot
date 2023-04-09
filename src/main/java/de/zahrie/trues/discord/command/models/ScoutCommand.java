package de.zahrie.trues.discord.command.models;


import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import de.zahrie.trues.discord.scouting.Scouting;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "scout", descripion = "Scouting", perm = @Perm(PermissionRole.ORGA_MEMBER),
    options = @Option(name = "typ", description = "Art des Scoutings", choices = {"Übersicht", "Lineup", "Games", "Champions", "Matchups", "Schedule", "Player History"})
)
@UseView({ModalRegisterer.SCOUT_CHAMPIONS, ModalRegisterer.SCOUT_HISTORY, ModalRegisterer.SCOUT_LINEUP,
    ModalRegisterer.SCOUT_MATCHUPS, ModalRegisterer.SCOUT_OVERVIEW, ModalRegisterer.SCOUT_SCHEDULE, ModalRegisterer.SCOUT_PLAYER_HISTORY})
public class ScoutCommand extends SlashCommand {
  @Override
  public boolean execute(SlashCommandInteractionEvent event) {
    final String typeString = find("typ").string();
    final Scouting.ScoutingType type = Scouting.ScoutingType.fromKey(typeString);
    return sendModal(type.ordinal());
  }
}
