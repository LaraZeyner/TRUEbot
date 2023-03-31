
package de.zahrie.trues.discord.command.models.training;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "training", descripion = "Training Commands", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class TrainingCommand extends SlashCommand {
  public TrainingCommand() {
    super(
        new TrainingAddCommand(),
        new TrainingStartCommand()
    );
  }

  @Override
  @Msg
  public boolean execute(SlashCommandInteractionEvent event) {
    return true;
  }
}
