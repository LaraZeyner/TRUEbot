package de.zahrie.trues.discord.command.models;

import java.util.Objects;

import de.zahrie.trues.api.discord.command.context.UseView;
import de.zahrie.trues.api.discord.command.slash.SlashCommand;
import de.zahrie.trues.api.discord.command.slash.annotations.Command;
import de.zahrie.trues.api.discord.command.slash.annotations.Option;
import de.zahrie.trues.api.discord.command.slash.annotations.Perm;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.PermissionRole;
import de.zahrie.trues.discord.modal.ModalRegisterer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(name = "bewerben", descripion = "bei uns bewerben", perm = @Perm(PermissionRole.EVERYONE), options = {
    @Option(name = "als", description = "Art der Bewerbung", choices = {"Teammitglied", "Staffmitglied"})
})
public class BewerbenCommand extends SlashCommand {
  @Override
  @UseView({ModalRegisterer.APPLY, ModalRegisterer.APPLY_STAFF})
  public boolean execute(SlashCommandInteractionEvent event) {
    final var typ = Objects.requireNonNull(event.getOption("als")).getAsString();
    final boolean isAdvanced = getInvoker().isEvenOrAbove(DiscordGroup.TEAM_CAPTAIN);
    return sendModal(isAdvanced, ApplicationType.of(typ).equals(ApplicationType.TEAM) ? 1 : 2);
  }

  private enum ApplicationType {
    TEAM, STAFF;

    private static ApplicationType of(String key) {
      return key.equals("Teammitglied") ? TEAM : STAFF;
    }
  }
}
