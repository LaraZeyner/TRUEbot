
package de.zahrie.trues.discord.command.models.deprecated;

import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.command.models.ChannelCreateCommand;
import de.zahrie.trues.discord.command.models.ChannelEditCommand;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "channel", perm = @Perm(PermissionRole.ORGA_MEMBER))
@Deprecated
public class ChannelCommand extends SlashCommand {

  public ChannelCommand() {
    super(
        new ChannelCreateCommand(),
        new ChannelEditCommand()
    );
  }

  @Override
  @Msg("Der Auftrag wurde erfolgreich durchgeführt.")
  public boolean execute(SlashCommandInteractionEvent event) {
    if (event.getChannel() instanceof ICategorizableChannel channel) {
      return true;
    }
    return errorMessage();
  }
}
