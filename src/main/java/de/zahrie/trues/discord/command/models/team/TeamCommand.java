
package de.zahrie.trues.discord.command.models.team;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "team", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class TeamCommand extends SlashCommand {
  public TeamCommand() {
    super(
        new TeamCreateCommand(),
        new TeamFollowCommand(),
        new TeamLinkCommand()
    );
  }

  @Override
  @Msg
  public boolean execute(SlashCommandInteractionEvent event) {
    return true;
  }
}
