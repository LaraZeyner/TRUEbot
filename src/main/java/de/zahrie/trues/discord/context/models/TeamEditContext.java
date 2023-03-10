package de.zahrie.trues.discord.context.models;

import de.zahrie.trues.api.discord.command.context.ContextCommand;
import de.zahrie.trues.api.discord.command.context.Context;
import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.annotations.Msg;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@Context(name = "Teammember bearbeiten")
public class TeamEditContext extends ContextCommand {
  @Override
  @Msg(value = "Der Nutzer wurde bearbeitet.")
  @UseView(ModalRegisterer.TEAM_EDIT)
  public void execute(UserContextInteractionEvent event) {
    setPermission(o -> o.isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN));

    final boolean advancedPermissions = getInvoker().isAbove(DiscordGroup.TEAM_CAPTAIN);
    sendModal(advancedPermissions);
  }
}
