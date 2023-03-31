package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "scrim", descripion = "Scrim erstellen", perm = @Perm(PermissionRole.ORGA_MEMBER))
public class ScrimCommand extends SlashCommand {
  @Override
  @Msg("Das Scrim wurde eingetragen.")
  @UseView(ModalRegisterer.SCRIM_CREATE)
  public boolean execute(SlashCommandInteractionEvent event) {
    return sendModal();
  }
}
