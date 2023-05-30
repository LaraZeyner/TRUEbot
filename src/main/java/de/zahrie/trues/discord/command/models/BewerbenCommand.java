package de.zahrie.trues.discord.command.models;

import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "bewerben", descripion = "bei uns bewerben", perm = @Perm(PermissionRole.REGISTERED), options = {
    @Option(name = "als", description = "Art der Bewerbung", choices = {"Teammitglied", "Staffmitglied"})
})
public class BewerbenCommand extends SlashCommand {
  @Override
  @UseView({ModalRegisterer.APPLY, ModalRegisterer.APPLY_STAFF})
  public boolean execute(SlashCommandInteractionEvent event) {
    final ApplicationType type = find("als").toEnum(ApplicationType.class);
    if (type.equals(ApplicationType.TEAMMITGLIED) && getInvoker().getPlayer() == null)
      return reply("Du musst zunächst deinen League-Account registrieren. Schreibe mir dafür **lol name: dasistmeinname**. " +
          "Alternativ kannst du auch den Command **/settings** nutzen.");

    final boolean isAdvanced = getInvoker().isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN);
    return sendModal(isAdvanced, type.equals(ApplicationType.TEAMMITGLIED) ? 0 : 1);
  }

  private enum ApplicationType {
    TEAMMITGLIED, STAFFMITGLIED
  }
}
